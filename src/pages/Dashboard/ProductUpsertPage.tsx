// File: src/pages/Dashboard/ProductUpsertPage.tsx

import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

// --- Import các services và types ---
import { productService, ProductCreatePayload, ProductUpdatePayload, Product } from '../../services/productService';
import { categoryService } from '../../services/categoryService';
import { supplierService } from '../../services/supplierService';
import { inventoryProductService, InventoryProductCreatePayload } from '../../services/inventoryProductService';
import { inventoryService, Inventory } from '../../services/inventoryService';
import { uploadMultipleImages } from '../../services/storageService';

// --- Import component mới ---
import SelectWithAddNew from '../../components/common/SelectWithAddNew';
import Modal from '../../components/common/Modal';
import AddCategoryForm from '../../components/Category/AddCategoryForm';
import AddSupplierForm from '../../components/Supplier/AddSupplierForm';
import AddInventoryForm from '../../components/Inventory/AddInventoryForm';
import Select, { MultiValue } from 'react-select';

interface OptionType { value: number; label: string; }

// Component để hiển thị tag sản phẩm đính kèm
const ProductTag: React.FC<{
  productId: number;
  productName: string;
  combinationId: number;
  onRemove: (combinationId: number) => void;
  isRemoving: boolean;
}> = ({ productId, productName, combinationId, onRemove, isRemoving }) => {
  return (
    <div className="inline-flex items-center bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-medium mr-2 mb-2">
      <span className="mr-2">
        {productName} (ID: {productId})
      </span>
      <button
        type="button"
        onClick={() => onRemove(combinationId)}
        disabled={isRemoving}
        className="text-blue-600 hover:text-blue-800 hover:bg-blue-200 rounded-full p-1 transition-colors"
        title="Xóa sản phẩm đính kèm"
      >
        {isRemoving ? (
          <div className="w-4 h-4 animate-spin rounded-full border-2 border-blue-600 border-t-transparent"></div>
        ) : (
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
        )}
      </button>
    </div>
  );
};

const ProductUpsertPage: React.FC = () => {
  const { productId } = useParams<{ productId: string }>();
  const isEditMode = !!productId;

  const navigate = useNavigate();
  const queryClient = useQueryClient();

  // --- STATE ---
  const [productForm, setProductForm] = useState<Partial<ProductCreatePayload>>({
    discountPercentage: undefined,
    discountTriggerDays: undefined,
    netWeight: '',
  });
  const [inventoryForm, setInventoryForm] = useState<Omit<InventoryProductCreatePayload, 'productId'>>({
    inventoryId: 0, stockNumber: 100, stockLevel: 'MEDIUM',
  });
  const [imageFiles, setImageFiles] = useState<File[]>([]);
  const [relatedProductIds, setRelatedProductIds] = useState<number[]>([]);
  // Gửi thông báo cho tất cả khách hàng?
  const [sendNotificationToAll, setSendNotificationToAll] = useState<boolean>(false);
  const [currentCombinations, setCurrentCombinations] = useState<any[]>([]); // Lưu các combination hiện tại
  const [imagePreviews, setImagePreviews] = useState<string[]>([]);
  const [removingCombinationId, setRemovingCombinationId] = useState<number | null>(null);
  
  // State để quản lý modal
  const [showAddCategoryModal, setShowAddCategoryModal] = useState(false);
  const [showAddSupplierModal, setShowAddSupplierModal] = useState(false);
  const [showAddInventoryModal, setShowAddInventoryModal] = useState(false);

  // --- DATA FETCHING ---
  const currentProductIdNum = Number(productId);

  const { data: allProductsForSelect } = useQuery({
    queryKey: ['allProductsForSelect'],
    queryFn: () => productService.getProducts({ page: 1, pageSize: 1000 }),
  });

  // Lấy danh sách sản phẩm đính kèm (chỉ khi chỉnh sửa)
  const { data: attachedProductsPage } = useQuery({
    queryKey: ['attachedProductsEdit', currentProductIdNum],
    enabled: isEditMode && !isNaN(currentProductIdNum),
    queryFn: () => productService.getAttachedProductsPaged(currentProductIdNum, { page: 1, pageSize: 100 }),
  });

  // Lấy danh sách combinations hiện tại (chỉ khi chỉnh sửa)
  const { data: currentCombinationsData } = useQuery({
    queryKey: ['currentCombinations', currentProductIdNum],
    enabled: isEditMode && !isNaN(currentProductIdNum),
    queryFn: () => productService.getProductCombinations(currentProductIdNum, { page: 1, pageSize: 100 }),
  });

  // Tải dữ liệu sản phẩm CẦN SỬA
  const { data: existingProduct, isLoading: isLoadingProductDetails } = useQuery({
    queryKey: ['product', productId],
    queryFn: () => productService.getProductById(Number(productId)),
    enabled: isEditMode && !!productId,
  });

  // Điền dữ liệu vào form khi ở chế độ sửa
  useEffect(() => {
    if (isEditMode && existingProduct) {
      setProductForm({
        name: existingProduct.name,
        price: existingProduct.price,
        discountPercentage: existingProduct.discountPercentage,
        discountTriggerDays: existingProduct.discountTriggerDays,
        unit: existingProduct.unit,
        netWeight: existingProduct.netWeight,
        specification: existingProduct.specification,
        description: existingProduct.description,
        categoryId: existingProduct.category.categoryId,
        supplierId: existingProduct.supplier.supplierId,
      });
      const existingImages = existingProduct.productImageResponses?.map(img => img.imageUrl) || [];
      setImagePreviews(existingImages);
    }
  }, [isEditMode, existingProduct]);

  // Cập nhật relatedProductIds và currentCombinations khi data tải xong
  useEffect(() => {
    if (currentCombinationsData) {
      // Một số API có thể trả về dữ liệu dạng PagedResponse (có field `content`),
      // một số khác trả về mảng thuần. Chuẩn hoá để luôn lấy được mảng combinations
      const combos: any[] = (currentCombinationsData as any).content ?? (currentCombinationsData as any);

      console.log('currentCombinationsData loaded:', currentCombinationsData);
      setCurrentCombinations(combos);

      // Lấy danh sách ID sản phẩm đính kèm hiện tại
      const currentRelatedIds = combos.map((combo: any) => (typeof combo.productId2 === 'object' ? combo.productId2.productId : combo.productId2));
      setRelatedProductIds(currentRelatedIds);
    }
  }, [currentCombinationsData]);

  // Lấy danh sách cho các dropdown
  const { data: categoriesResponse } = useQuery({
    queryKey: ['allCategories'],
    queryFn: () => categoryService.getCategories({ pageSize: 999 }),
  });
  const categories = categoriesResponse?.content || [];

  const { data: suppliersResponse } = useQuery({
    queryKey: ['allSuppliers'],
    queryFn: () => supplierService.getSuppliers({ pageSize: 1000 }),
  });
  const suppliers = suppliersResponse?.content || [];

  const { data: inventories = [] } = useQuery({
    queryKey: ['allInventoriesForSelect'],
    queryFn: () => inventoryService.getAllInventories({ pageSize: 999 }),
  });

  // --- MUTATIONS ---
  const mutationOptions = {
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['products'] });
      navigate('/dashboard/products');
    },
    onError: (error: Error) => {
      alert(`Đã có lỗi xảy ra: ${error.message}`);
    },
  };

  const createProductMutation = useMutation({
    mutationFn: (variables: { productData: ProductCreatePayload, imageUrls: string[], inventoryData: Omit<InventoryProductCreatePayload, 'productId'> }) =>
      productService.createProduct(
        { ...variables.productData, combinationProductIds: relatedProductIds },
        variables.imageUrls,
        variables.inventoryData,
        sendNotificationToAll,
      ),
    ...mutationOptions,
    onSuccess: () => {
      alert('Thêm sản phẩm thành công!');
      mutationOptions.onSuccess();
    }
  });

  const updateProductMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number, payload: ProductUpdatePayload }) =>
      productService.updateProduct(id, payload),
    ...mutationOptions,
    onSuccess: () => {
      alert('Cập nhật sản phẩm thành công!');
      queryClient.invalidateQueries({ queryKey: ['product', productId] });
      mutationOptions.onSuccess();
    }
  });

  // Mutation để xóa product combination
  const deleteCombinationMutation = useMutation({
    mutationFn: (combinationId: number) => productService.deleteProductCombination(combinationId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['currentCombinations', currentProductIdNum] });
      queryClient.invalidateQueries({ queryKey: ['attachedProductsEdit', currentProductIdNum] });
      setRemovingCombinationId(null);
      alert('Xóa sản phẩm đính kèm thành công!');
    },
    onError: (error: Error) => {
      setRemovingCombinationId(null);
      alert(`Lỗi khi xóa: ${error.message}`);
    }
  });

  // Mutation để thêm product combination
  const addCombinationMutation = useMutation({
    mutationFn: (payload: { productId1: number; productId2: number }) => 
      productService.createProductCombination(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['currentCombinations', currentProductIdNum] });
      queryClient.invalidateQueries({ queryKey: ['attachedProductsEdit', currentProductIdNum] });
      alert('Thêm sản phẩm đính kèm thành công!');
    },
    onError: (error: Error) => {
      alert(`Lỗi khi thêm: ${error.message}`);
    }
  });

  const isSubmitting = createProductMutation.isPending || updateProductMutation.isPending;

  // --- EVENT HANDLERS ---
  const handleFormChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setProductForm(prev => ({ ...prev, [name]: value }));
  };

  const handleInventoryFormChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setInventoryForm(prev => ({ ...prev, [name]: name === 'stockLevel' ? value : Number(value) }));
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const newFiles = Array.from(e.target.files);
      setImageFiles(prevFiles => [...prevFiles, ...newFiles]);
      const newPreviewUrls = newFiles.map(file => URL.createObjectURL(file));
      setImagePreviews(prevUrls => [...prevUrls, ...newPreviewUrls]);
    }
  };

  // Xử lý xóa combination
  const handleDeleteCombination = (combinationId: number) => {
    if (window.confirm('Bạn có chắc muốn xóa sản phẩm đính kèm này?')) {
      setRemovingCombinationId(combinationId);
      deleteCombinationMutation.mutate(combinationId);
    }
  };

  // Xử lý thêm combination mới
  const handleAddCombinations = () => {
    if (!isEditMode) return;
    
    const currentProductId2s = currentCombinations.map(combo => combo.productId2);
    const newProductIds = relatedProductIds.filter(id => !currentProductId2s.includes(id));
    
    if (newProductIds.length === 0) {
      alert('Không có sản phẩm mới để thêm');
      return;
    }

    // Thêm từng sản phẩm một cách tuần tự
    const addNext = async (index: number) => {
      if (index >= newProductIds.length) return;
      
      try {
        await addCombinationMutation.mutateAsync({
          productId1: currentProductIdNum,
          productId2: newProductIds[index]
        });
        // Tiếp tục thêm sản phẩm tiếp theo
        await addNext(index + 1);
      } catch (error) {
        console.error('Error adding combination:', error);
      }
    };

    addNext(0);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // --- Validate ---
    if (!productForm.name?.trim() || !productForm.unit?.trim() || !productForm.netWeight?.trim() || !productForm.categoryId || !productForm.supplierId) {
      alert("Vui lòng điền các trường sản phẩm bắt buộc (*).");
      return;
    }
    if (productForm.price === undefined || Number(productForm.price) < 0) {
    alert("Giá bán phải lớn hơn hoặc bằng 0.");
      return;
    }

    if (productForm.discountPercentage !== undefined && Number(productForm.discountPercentage) < 0) {
      alert("Phần trăm giảm giá phải lớn hơn hoặc bằng 0.");
      return;
    }
    if (productForm.discountTriggerDays !== undefined && Number(productForm.discountTriggerDays) < 0) {
      alert("Số ngày áp dụng phải lớn hơn hoặc bằng 0.");
      return;
    }

    // Khi thêm mới, bắt buộc phải có ảnh và thông tin kho
    if (!isEditMode) {
      if (imageFiles.length === 0) {
        alert('Vui lòng chọn ít nhất một hình ảnh.');
        return;
      }
      if (!inventoryForm.inventoryId) {
        alert("Vui lòng chọn kho để nhập hàng.");
        return;
      }
    }

    try {
      // Chỉ upload ảnh nếu có file mới được chọn
      const imageUrls = imageFiles.length > 0 ? await uploadMultipleImages(imageFiles, 'msa') : [];

      const discountPercentageValue = productForm.discountPercentage === undefined || String(productForm.discountPercentage).trim() === '' ? undefined : Number(productForm.discountPercentage);
      const discountTriggerDaysValue = productForm.discountTriggerDays === undefined || String(productForm.discountTriggerDays).trim() === '' ? undefined : Number(productForm.discountTriggerDays);

      const productPayload: ProductCreatePayload | ProductUpdatePayload = {
        name: productForm.name!,
        description: productForm.description || '',
        price: Number(productForm.price) || 0,
        discountPercentage: discountPercentageValue,
        discountTriggerDays: discountTriggerDaysValue,
        unit: productForm.unit!,
        netWeight: productForm.netWeight || undefined,
        specification: productForm.specification || '',
        categoryId: Number(productForm.categoryId),
        supplierId: Number(productForm.supplierId),
        totalRevenue: 1
      };

      if (isEditMode) {
        // Logic SỬA: thêm ảnh mới (nếu có) rồi cập nhật thông tin sản phẩm
        if (imageUrls.length > 0) {
          await productService.addProductImages(Number(productId), imageUrls);
        }
        updateProductMutation.mutate({ id: Number(productId), payload: productPayload });
      } else {
        // Logic THÊM: quy trình 3 bước
        const inventoryData = {
          inventoryId: Number(inventoryForm.inventoryId),
          stockNumber: Number(inventoryForm.stockNumber) || 0,
          stockLevel: inventoryForm.stockLevel!,
        };
        createProductMutation.mutate({ productData: productPayload, imageUrls, inventoryData });
      }

    } catch (error) {
      alert(`Thao tác thất bại: ${error instanceof Error ? error.message : 'Lỗi không xác định'}`);
    }
  };

  if (isLoadingProductDetails) {
    return <div className="p-8 text-center">Đang tải dữ liệu sản phẩm...</div>;
  }

  return (
    <div className="bg-white p-8 rounded-lg shadow-md max-w-4xl mx-auto mt-10">
      <h2 className="text-3xl font-bold text-gray-800 mb-6">
        {isEditMode ? `Sửa Sản Phẩm: ${existingProduct?.name || ''}` : 'Thêm Sản Phẩm Mới'}
      </h2>
      <form onSubmit={handleSubmit} className="space-y-8">

        <fieldset className="border p-4 rounded-md">
          <legend className="text-lg font-semibold px-2">Thông tin Sản phẩm</legend>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-2">
            <div>
              <label htmlFor="name">Tên Sản phẩm (*)</label>
              <input type="text" name="name" id="name" required value={productForm.name || ''} onChange={handleFormChange} className="mt-1 block w-full p-2 border rounded-md" />
            </div>
            <div>
               <label htmlFor="price">Giá bán (VNĐ) (*)</label>
               <input type="number" placeholder="0" name="price" id="price" min={0} required value={productForm.price ?? ''} onChange={handleFormChange} className="mt-1 block w-full p-2 border rounded-md" />
             </div>
             <div>
               <label htmlFor="discountPercentage">Giảm giá (%)</label>
               <input type="number" placeholder="0" min={0} step="0.01" name="discountPercentage" id="discountPercentage" value={productForm.discountPercentage ?? ''} onChange={handleFormChange} className="mt-1 block w-full p-2 border rounded-md" />
             </div>
             <div>
               <label htmlFor="discountTriggerDays">Số ngày áp dụng giảm</label>
               <input type="number" name="discountTriggerDays" id="discountTriggerDays" value={productForm.discountTriggerDays ?? ''} onChange={handleFormChange} className="mt-1 block w-full p-2 border rounded-md" />
             </div>
             <div>
               <label htmlFor="netWeight">Khối lượng tịnh</label>
               <input type="text" name="netWeight" id="netWeight" value={productForm.netWeight || ''} onChange={handleFormChange} className="mt-1 block w-full p-2 border rounded-md" />
             </div>
            
            <SelectWithAddNew
              label="Danh mục"
              name="categoryId"
              value={productForm.categoryId}
              onChange={handleFormChange}
              options={categories.map(c => ({ value: c.categoryId, label: c.name }))}
              onAddNew={() => setShowAddCategoryModal(true)}
              required
            />
            <SelectWithAddNew
              label="Nhà cung cấp"
              name="supplierId"
              value={productForm.supplierId}
              onChange={handleFormChange}
              options={suppliers.map(s => ({ value: s.supplierId, label: s.name }))}
              onAddNew={() => setShowAddSupplierModal(true)}
              required
            />
            <div className="md:col-span-2">
              <label htmlFor="description">Mô tả</label>
              <textarea name="description" id="description" rows={3} value={productForm.description || ''} onChange={handleFormChange} className="mt-1 block w-full p-2 border rounded-md"></textarea>
            </div>
            <div>
              <label htmlFor="unit">Đơn vị tính (*)</label>
              <input type="text" name="unit" id="unit" required value={productForm.unit || ''} onChange={handleFormChange} className="mt-1 block w-full p-2 border rounded-md" />
            </div>
            <div>
              <label htmlFor="specification">Quy cách</label>
              <input type="text" name="specification" id="specification" value={productForm.specification || ''} onChange={handleFormChange} className="mt-1 block w-full p-2 border rounded-md" />
            </div>
          </div>
        </fieldset>

        {/* SẢN PHẨM ĐI KÈM */}
        <fieldset className="border p-4 rounded-md">
          <legend className="text-lg font-semibold px-2">Sản phẩm đi kèm</legend>
          
          {/* Hiển thị danh sách sản phẩm đính kèm hiện tại (chỉ trong chế độ sửa) */}
          {isEditMode && (
            <div className="mb-6">
              <h4 className="text-md font-medium mb-3">
                Sản phẩm đính kèm hiện tại: 
                <span className="text-sm text-gray-600 ml-2">
                  ({currentCombinations.length} sản phẩm)
                </span>
              </h4>
              
              {currentCombinations.length > 0 ? (
                <div className="min-h-[60px] p-3 border rounded-md bg-gray-50">
                  {currentCombinations.map((combo) => {
                    const product = allProductsForSelect?.productsPage.content.find(p => p.productId === combo.productId2);
                    return (
                      <ProductTag
                        key={combo.combinationId}
                        productId={typeof combo.productId2 === 'object' ? combo.productId2.productId : combo.productId2}
                        productName={product ? product.name : (typeof combo.productId2 === 'object' ? combo.productId2.name : 'Không tìm thấy sản phẩm')}
                        combinationId={combo.combinationId}
                        onRemove={handleDeleteCombination}
                        isRemoving={removingCombinationId === combo.combinationId}
                      />
                    );
                  })}
                </div>
              ) : (
                <div className="text-gray-500 text-sm bg-gray-50 p-3 rounded border">
                  Chưa có sản phẩm đính kèm nào
                </div>
              )}
            </div>
          )}

          {/* Selector để chọn sản phẩm đính kèm */}
          <div className="mb-4">
            <label className="block text-sm font-medium mb-2">
              {isEditMode ? 'Thêm sản phẩm đính kèm mới:' : 'Chọn sản phẩm đi kèm:'}
            </label>
            <Select
              isMulti
              options={allProductsForSelect?.productsPage.content
                .filter((p: Product) => !isEditMode || p.productId !== currentProductIdNum)
                .map((p) => ({ value: p.productId, label: `${p.name} (ID: ${p.productId})` })) || []}
              value={allProductsForSelect?.productsPage.content
                .filter((p: Product) => relatedProductIds.includes(p.productId))
                .map((p) => ({ value: p.productId, label: `${p.name} (ID: ${p.productId})` })) || []}
              onChange={(vals: MultiValue<OptionType>) => {
                const selectedIds = vals.map((v) => v.value);
                console.log('Selected product IDs:', selectedIds);
                setRelatedProductIds(selectedIds);
              }}
              placeholder="Chọn sản phẩm liên quan..."
              classNamePrefix="react-select"
            />
          </div>

          {/* Nút thêm sản phẩm đính kèm (chỉ hiển thị trong chế độ sửa) */}
          {isEditMode && (
            <div className="mt-4">
              <button
                type="button"
                onClick={handleAddCombinations}
                disabled={addCombinationMutation.isPending || relatedProductIds.length === 0}
                className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 disabled:bg-gray-400"
              >
                {addCombinationMutation.isPending ? 'Đang thêm...' : 'Thêm sản phẩm đính kèm'}
              </button>
              {relatedProductIds.length === 0 && (
                <p className="text-sm text-gray-500 mt-2">
                  Vui lòng chọn ít nhất một sản phẩm để thêm
                </p>
              )}
            </div>
          )}
        </fieldset>

        {/* Chỉ hiển thị phần nhập kho khi THÊM MỚI */}
        {!isEditMode && (
          <fieldset className="border p-4 rounded-md">
            <legend className="text-lg font-semibold px-2">Nhập kho ban đầu</legend>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-2">
              <SelectWithAddNew
                label="Nhập vào kho"
                name="inventoryId"
                value={inventoryForm.inventoryId}
                onChange={handleInventoryFormChange}
                options={inventories.map(i => ({ value: i.inventoryId, label: i.name }))}
                onAddNew={() => setShowAddInventoryModal(true)}
                required
              />
              <div>
                <label htmlFor="stockNumber">Số lượng ban đầu (*)</label>
                <input type="number" name="stockNumber" id="stockNumber" value={inventoryForm.stockNumber} required onChange={handleInventoryFormChange} className="mt-1 block w-full p-2 border rounded-md" />
              </div>
              <div>
                <label htmlFor="stockLevel">Tình trạng tồn kho (*)</label>
                <select name="stockLevel" id="stockLevel" value={inventoryForm.stockLevel} required onChange={handleInventoryFormChange} className="mt-1 block w-full p-2 border rounded-md">
                  <option value="MEDIUM">Trung bình</option>
                  <option value="HIGH">Cao</option>
                  <option value="LOW">Thấp</option>
                </select>
              </div>
            </div>
          </fieldset>
        )}

        {/* === THÔNG BÁO === */}
        <fieldset className="border p-4 rounded-md">
          <legend className="text-lg font-semibold px-2">Thông báo</legend>
          <label className="inline-flex items-center mt-2">
            <input
              type="checkbox"
              className="form-checkbox h-5 w-5 text-green-600"
              checked={sendNotificationToAll}
              onChange={(e) => setSendNotificationToAll(e.target.checked)}
            />
            <span className="ml-2">Gửi thông báo sản phẩm mới đến tất cả khách hàng</span>
          </label>
        </fieldset>

        {/* === HÌNH ẢNH SẢN PHẨM === */}
        <fieldset className="border p-4 rounded-md">
          <legend className="text-lg font-semibold px-2">Hình ảnh sản phẩm</legend>
          <div className="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-dashed rounded-md">
            <div className="space-y-1 text-center">
              <input id="file-upload" name="file-upload" type="file" multiple onChange={handleImageChange} className="sr-only" />
              <label htmlFor="file-upload" className="relative cursor-pointer bg-white rounded-md font-medium text-green-600 hover:text-green-500">
                <span>Tải lên các file</span>
              </label>
              <p className="text-xs text-gray-500">PNG, JPG, GIF</p>
            </div>
          </div>
          {imagePreviews.length > 0 && (
            <div className="mt-4">
              <label className="block text-sm font-medium text-gray-700">Ảnh đã chọn:</label>
              <div className="mt-2 grid grid-cols-3 md:grid-cols-5 lg:grid-cols-8 gap-4">
                {imagePreviews.map((src, index) => (
                  <img key={index} src={src} alt={`Preview ${index}`} className="h-24 w-24 object-cover rounded-md shadow-sm" />
                ))}
              </div>
            </div>
          )}
        </fieldset>

        {/* --- NÚT SUBMIT --- */}
        <div className="flex justify-end space-x-4 pt-4">
          <button type="button" onClick={() => navigate('/dashboard/products')} className="bg-gray-200 text-gray-800 font-bold py-2 px-6 rounded-md hover:bg-gray-300">
            Hủy
          </button>
          <button type="submit" disabled={isSubmitting} className="bg-green-600 text-white font-bold py-2 px-6 rounded-md hover:bg-green-700 disabled:bg-gray-400">
            {isSubmitting ? 'Đang xử lý...' : isEditMode ? 'Cập nhật' : 'Lưu Sản Phẩm'}
          </button>
        </div>
      </form>
      
      {isSubmitting && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-green-500"></div>
        </div>
      )}

      {/* Modals */}
      <Modal
        title="Thêm Danh Mục Mới"
        isOpen={showAddCategoryModal}
        onClose={() => setShowAddCategoryModal(false)}
      >
        <AddCategoryForm
          onSuccess={() => {
            setShowAddCategoryModal(false);
            queryClient.invalidateQueries({ queryKey: ['allCategories'] });
          }}
        />
      </Modal>

      <Modal title="Thêm Nhà Cung Cấp Mới" isOpen={showAddSupplierModal} onClose={() => setShowAddSupplierModal(false)}>
        <AddSupplierForm
          onSuccess={() => {
            setShowAddSupplierModal(false);
            queryClient.invalidateQueries({ queryKey: ['allSuppliers'] });
          }}
        />
      </Modal>

      <Modal title="Thêm Kho Hàng Mới" isOpen={showAddInventoryModal} onClose={() => setShowAddInventoryModal(false)}>
        <AddInventoryForm
          onSuccess={() => {
            setShowAddInventoryModal(false);
            queryClient.invalidateQueries({ queryKey: ['allInventoriesForSelect'] });
          }}
        />
      </Modal>
    </div>
  );
};

export default ProductUpsertPage;