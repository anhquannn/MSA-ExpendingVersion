// src/pages/Dashboard/BranchUpsertPage.tsx

import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

// Import service và các types cần thiết
import {
  branchService,
  Branch,
  CreateBranchWithManagerPayload,
} from '../../services/branchService';
import { shipmentService, CityResponse, DistrictResponse, WardResponse } from '../../services/shipmentService';

// Giả định UpdatePayload, ta có thể tái sử dụng một phần từ CreatePayload
type BranchUpdatePayload = Partial<Omit<Branch, 'branchId' | 'inventory'>>;

const BranchUpsertPage: React.FC = () => {
  const { branchId } = useParams<{ branchId: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  // Xác định chế độ: nếu có branchId là 'sửa', ngược lại là 'thêm'
  const isEditMode = !!branchId;

  // State cho dữ liệu form
  const [formData, setFormData] = useState<Partial<CreateBranchWithManagerPayload>>({
    branchName: '',
    branchPhone: '',
    branchStreet: '',
    branchWard: '',
    branchWardCode: '',
    branchDistrict: '',
    branchDistrictCode: '',
    branchCity: '',
    branchCityCode: '',
    inventoryName: '',
    inventoryAddress: '',
    inventoryContact: '',
    managerFullName: '',
    managerEmail: '',
    managerPhoneNumber: '',
    managerPassword: '',
  });

  // --- TẢI DỮ LIỆU CHO CHẾ ĐỘ SỬA ---
  const { data: existingBranch, isLoading: isLoadingBranch } = useQuery({
    queryKey: ['branch', branchId],
    // Gọi API getBranchById, ép kiểu ID từ string sang number
    queryFn: () => branchService.getBranchById(Number(branchId)),
    // Chỉ chạy query này khi ở chế độ Sửa (có branchId)
    enabled: isEditMode,
  });

  // --- useEffect để điền dữ liệu vào form khi ở chế độ Sửa ---
  useEffect(() => {
    if (isEditMode && existingBranch) {
      // Ánh xạ dữ liệu từ API vào state của form
      setFormData({
        branchName: existingBranch.name,
        branchPhone: existingBranch.phone,
        branchCity: existingBranch.city,
        branchCityCode: existingBranch.cityCode,
        branchDistrict: existingBranch.district,
        branchDistrictCode: existingBranch.districtCode,
        branchWard: existingBranch.ward,
        branchWardCode: existingBranch.wardCode,
        branchStreet: existingBranch.street,
        // Các trường inventory và manager không được trả về từ API getById
        // nên sẽ không được điền sẵn khi chỉnh sửa.
        // Đây là một điểm cần lưu ý với thiết kế API hiện tại.
      });
    }
  }, [existingBranch, isEditMode]);

  // --- MUTATIONS ĐỂ LƯU DỮ LIỆU ---

  const createBranchMutation = useMutation({
    mutationFn: (payload: CreateBranchWithManagerPayload) =>
      branchService.createBranchWithManager(payload),
    onSuccess: () => {
      alert('Tạo chi nhánh mới thành công!');
      queryClient.invalidateQueries({ queryKey: ['branches'] }); // Làm mới danh sách
      navigate('/dashboard/branches'); // Quay về trang danh sách
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`),
  });

  const updateBranchMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: BranchUpdatePayload }) =>
      branchService.updateBranch(id, payload),
    onSuccess: (updatedBranch) => {
      alert('Cập nhật chi nhánh thành công!');
      // Cập nhật cả cache của trang danh sách và trang chi tiết
      queryClient.invalidateQueries({ queryKey: ['branches'] });
      queryClient.setQueryData(['branch', updatedBranch.branchId.toString()], updatedBranch);
      navigate('/dashboard/branches');
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`),
  });

  // --- FETCH LOCATION LISTS ---
  const { data: cities = [] } = useQuery<CityResponse[]>({
    queryKey: ['cities'],
    queryFn: shipmentService.getCities,
  });

  const { data: districts = [], refetch: refetchDistricts } = useQuery<DistrictResponse[]>({
    queryKey: ['districts', formData.branchCityCode],
    queryFn: () => {
      const codeNum = Number(formData.branchCityCode);
      if (Number.isNaN(codeNum)) return Promise.resolve([]);
      return shipmentService.getDistricts(codeNum);
    },
    enabled: !!formData.branchCityCode,
  });

  const { data: wards = [], refetch: refetchWards } = useQuery<WardResponse[]>({
    queryKey: ['wards', formData.branchDistrictCode],
    queryFn: () => {
      const codeNum = Number(formData.branchDistrictCode);
      if (Number.isNaN(codeNum)) return Promise.resolve([]);
      return shipmentService.getWards(codeNum);
    },
    enabled: !!formData.branchDistrictCode,
  });

  // --- EVENT HANDLERS ---
  
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;

    // Cập nhật giá trị thô
    setFormData(prev => ({ ...prev, [name]: value }));

    if (name === 'branchCityCode') {
      const selectedCity = cities.find(c => String(c.code) === value);
      setFormData(prev => ({
        ...prev,
        branchCityCode: value,
        branchCity: selectedCity?.name || '',
        // reset các trường phụ thuộc
        branchDistrictCode: '',
        branchDistrict: '',
        branchWardCode: '',
        branchWard: '',
      }));
      // đảm bảo refetch sau khi state đã cập nhật
      setTimeout(() => {
        refetchDistricts();
      }, 0);
    }
    if (name === 'branchDistrictCode') {
      const selectedDistrict = districts.find(d => String(d.code) === value);
      setFormData(prev => ({
        ...prev,
        branchDistrictCode: value,
        branchDistrict: selectedDistrict?.name || '',
        // reset ward
        branchWardCode: '',
        branchWard: '',
      }));
      setTimeout(() => {
        refetchWards();
      }, 0);

    }
    if (name === 'branchWardCode') {
      const selectedWard = wards.find(w => String(w.code) === value);
      setFormData(prev => ({
        ...prev,
        branchWardCode: value,
        branchWard: selectedWard?.name || '',
      }));
    }
  };


  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
  e.preventDefault();

  // --- Validate required fields ---
  const requiredFields = [
    formData.branchName,
    formData.branchPhone,
    formData.branchCity,
    formData.branchCityCode,
    formData.branchDistrict,
    formData.branchDistrictCode,
    formData.branchWard,
    formData.branchWardCode,
    formData.branchStreet,
  ];
  const hasEmpty = requiredFields.some((f) => !f || String(f).trim() === '');
  if (hasEmpty) {
    alert('Vui lòng nhập đầy đủ thông tin chi nhánh.');
    return;
  }
    e.preventDefault();

    if (isEditMode) {
      // Logic cho việc Sửa
      if (!branchId) return;
      const updatePayload: BranchUpdatePayload = {
        name: formData.branchName,
        phone: formData.branchPhone,
        city: formData.branchCity,
        cityCode: formData.branchCityCode,
        district: formData.branchDistrict,
        districtCode: formData.branchDistrictCode,
        ward: formData.branchWard,
        wardCode: formData.branchWardCode,
        street: formData.branchStreet,
      };
      updateBranchMutation.mutate({ id: Number(branchId), payload: updatePayload });
    } else {
      // Logic cho việc Thêm mới
      const createPayload = formData as CreateBranchWithManagerPayload;
      // Passed validation, tiến hành gọi API
      createBranchMutation.mutate(createPayload);
    }
  };
  
  // --- RENDER ---

  if (isLoadingBranch) {
    return <div className="text-center p-8">Đang tải thông tin chi nhánh để chỉnh sửa...</div>;
  }

  return (
    <div className="max-w-4xl mx-auto bg-white p-8 rounded-lg shadow-md mt-10">
      <h1 className="text-3xl font-bold text-gray-800 mb-6">
        {isEditMode ? 'Chỉnh Sửa Chi Nhánh' : 'Thêm Chi Nhánh Mới'}
      </h1>
      <form onSubmit={handleSubmit} className="space-y-8">
        {/* Thông tin chi nhánh */}
        <fieldset className="border p-4 rounded-md">
          <legend className="text-lg font-semibold px-2">Thông tin Chi nhánh</legend>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-2">
            <input name="branchName" value={formData.branchName || ''} onChange={handleInputChange} placeholder="Tên chi nhánh (*)" required className="p-2 border rounded-md" />
            <input name="branchPhone" value={formData.branchPhone || ''} onChange={handleInputChange} placeholder="SĐT chi nhánh (*)" required className="p-2 border rounded-md" />

            {/* Tỉnh/Thành phố */}
            <select name="branchCityCode" value={formData.branchCityCode || ''} onChange={handleInputChange} required className="p-2 border rounded-md">
              <option value="">Chọn Tỉnh/Thành phố (*)</option>
              {cities.map(c => <option key={c.code} value={c.code}>{c.name}</option>)}
            </select>
            {/* Quận/Huyện */}
            <select name="branchDistrictCode" value={formData.branchDistrictCode || ''} onChange={handleInputChange} required className="p-2 border rounded-md">
              <option value="">Chọn Quận/Huyện (*)</option>
              {districts.map(d => <option key={d.code} value={d.code}>{d.name}</option>)}
            </select>
            {/* Phường/Xã */}
            <select name="branchWardCode" value={formData.branchWardCode || ''} onChange={handleInputChange} required className="p-2 border rounded-md">
              <option value="">Chọn Phường/Xã (*)</option>
              {wards.map(w => <option key={w.code} value={w.code}>{w.name}</option>)}
            </select>
            <input name="branchStreet" value={formData.branchStreet || ''} onChange={handleInputChange} placeholder="Đường/Số nhà" className="p-2 border rounded-md" />
          </div>
        </fieldset>

        {/* Thông tin kho & manager chỉ khi thêm mới */}
        {!isEditMode && (
          <>
            {/* Kho */}
            <fieldset className="border p-4 rounded-md">
              <legend className="text-lg font-semibold px-2">Thông tin Kho hàng</legend>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-2">
                <input name="inventoryName" value={formData.inventoryName || ''} onChange={handleInputChange} placeholder="Tên kho (*)" required className="p-2 border rounded-md" />
                <input name="inventoryContact" value={formData.inventoryContact || ''} onChange={handleInputChange} placeholder="SĐT liên hệ kho (*)" required className="p-2 border rounded-md" />
                <input name="inventoryAddress" value={formData.inventoryAddress || ''} onChange={handleInputChange} placeholder="Địa chỉ kho" className="p-2 border rounded-md md:col-span-2" />
              </div>
            </fieldset>

            {/* Manager */}
            <fieldset className="border p-4 rounded-md">
              <legend className="text-lg font-semibold px-2">Thông tin Người quản lý</legend>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-2">
                <input name="managerFullName" value={formData.managerFullName || ''} onChange={handleInputChange} placeholder="Họ tên quản lý (*)" required className="p-2 border rounded-md" />
                <input type="email" name="managerEmail" value={formData.managerEmail || ''} onChange={handleInputChange} placeholder="Email quản lý (*)" required className="p-2 border rounded-md" />
                <input type="tel" name="managerPhoneNumber" value={formData.managerPhoneNumber || ''} onChange={handleInputChange} placeholder="SĐT quản lý (*)" required className="p-2 border rounded-md" />
                <input type="password" name="managerPassword" value={formData.managerPassword || ''} onChange={handleInputChange} placeholder="Mật khẩu (*)" required className="p-2 border rounded-md" />
              </div>
            </fieldset>
          </>
        )}

        {/* Buttons */}
        <div className="flex justify-end space-x-4">
          <button type="button" onClick={() => navigate('/dashboard/branches')} className="bg-gray-200 text-gray-800 font-bold py-2 px-6 rounded-md hover:bg-gray-300">
            Hủy
          </button>
          <button type="submit" disabled={createBranchMutation.isPending || updateBranchMutation.isPending} className="bg-green-600 text-white font-bold py-2 px-6 rounded-md hover:bg-green-700 disabled:bg-gray-400">
            {createBranchMutation.isPending || updateBranchMutation.isPending ? 'Đang lưu...' : 'Lưu lại'}
          </button>
        </div>
      </form>
    </div>
  );

};

export default BranchUpsertPage;