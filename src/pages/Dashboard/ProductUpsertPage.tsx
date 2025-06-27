// export default ProductAddPage;
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

const ProductUpsertPage: React.FC = () => {
  const { productId } = useParams<{ productId: string }>();
  const isEditMode = !!productId;

  const navigate = useNavigate();
  const queryClient = useQueryClient();

  // --- STATE ---
  const [productForm, setProductForm] = useState<Partial<ProductCreatePayload>>({});
  const [inventoryForm, setInventoryForm] = useState<Omit<InventoryProductCreatePayload, 'productId'>>({
    inventoryId: 0, stockNumber: 100, stockLevel: 'MEDIUM',
  });
  const [imageFiles, setImageFiles] = useState<File[]>([]);
  const [relatedProductIds, setRelatedProductIds] = useState<number[]>([]);
  const [imagePreviews, setImagePreviews] = useState<string[]>([]);
  // State để quản lý modal (chưa xây dựng UI modal, chỉ là logic)
  const [showAddCategoryModal, setShowAddCategoryModal] = useState(false);
  const [showAddSupplierModal, setShowAddSupplierModal] = useState(false);
  const [showAddInventoryModal, setShowAddInventoryModal] = useState(false);

  // --- DATA FETCHING ---
  const currentProductIdNum = Number(productId);

  const { data: allProductsForSelect } = useQuery({
    queryKey: ['allProductsForSelect'],
    queryFn: () => productService.getProducts({ page: 1, pageSize: 100 }),
  });

  // Tải dữ liệu sản phẩm CẦN SỬA
  const { data: existingProduct, isLoading: isLoadingProductDetails } = useQuery({
    queryKey: ['product', productId],
    queryFn: () => productService.getProductById(Number(productId)), // Cần thêm hàm này vào service
    enabled: isEditMode,
  });

  // Điền dữ liệu vào form khi ở chế độ sửa
  useEffect(() => {
    if (isEditMode && existingProduct) {
      setProductForm({
        name: existingProduct.name,
        price: existingProduct.price,
        unit: existingProduct.unit,
        specification: existingProduct.specification,
        description: existingProduct.description,
        categoryId: existingProduct.category.categoryId,
        supplierId: existingProduct.supplier.supplierId,
      });
      // Giả định ảnh và kho không chỉnh sửa ở đây, hoặc cần logic phức tạp hơn
      const existingImages = existingProduct.productImageResponses?.map(img => img.imageUrl) || [];
      setImagePreviews(existingImages);
      // fetch related products
      productService.getRelatedProductIds(existingProduct.productId).then(setRelatedProductIds);
    }
  }, [isEditMode, existingProduct]);

  type CreateProductFlowVariables = {
    productData: ProductCreatePayload;
    imageUrls: string[];
    inventoryData: Omit<InventoryProductCreatePayload, 'productId'>;
    totalRevenue: 1
  };

  const addProductMutation = useMutation({
    mutationFn: (variables: CreateProductFlowVariables) =>
      productService.createProduct(
        variables.productData,
        variables.imageUrls,
        variables.inventoryData,

      ),
    onSuccess: () => {
      alert('Thêm sản phẩm mới và nhập kho thành công!');
      queryClient.invalidateQueries({ queryKey: ['products'] });
      navigate('/dashboard/products');
    },
    onError: (error: Error) => {
      alert(`Đã có lỗi xảy ra trong quy trình: ${error.message}`);
    },
  });

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
      productService.createProduct({ ...variables.productData, combinationProductIds: relatedProductIds }, variables.imageUrls, variables.inventoryData),
    ...mutationOptions,
    onSuccess: () => {
      alert('Thêm sản phẩm thành công!');
      mutationOptions.onSuccess();
    }
  });

  const updateProductMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number, payload: ProductUpdatePayload & { combinationProductIds?: number[] } }) =>
      productService.updateProduct(id, { ...payload, combinationProductIds: relatedProductIds }),
    ...mutationOptions,
    onSuccess: () => {
      alert('Cập nhật sản phẩm thành công!');
      queryClient.invalidateQueries({ queryKey: ['product', productId] });
      mutationOptions.onSuccess();
    }
  });





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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // --- Validate ---
    if (!productForm.name || !productForm.categoryId || !productForm.supplierId) {
      alert("Vui lòng điền các trường sản phẩm bắt buộc (*).");
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

    // --- Logic upload và gọi mutation ---
    try {
      // Chỉ upload ảnh nếu có file mới được chọn (cho cả thêm và sửa)
      const imageUrls = imageFiles.length > 0 ? await uploadMultipleImages(imageFiles, 'msa') : [];

      const productPayload: ProductCreatePayload | ProductUpdatePayload = {
        name: productForm.name!,
        description: productForm.description || '',
        price: Number(productForm.price) || 0,
        unit: productForm.unit!,
        specification: productForm.specification || '',
        categoryId: Number(productForm.categoryId),
        supplierId: Number(productForm.supplierId),
        totalRevenue: 1
      };

      if (isEditMode) {
        // Logic SỬA: chỉ cập nhật thông tin sản phẩm
        // Việc sửa ảnh và kho có thể là một quy trình phức tạp hơn
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
              <input type="number" name="price" id="price" required value={productForm.price || 0} onChange={handleFormChange} className="mt-1 block w-full p-2 border rounded-md" />
            </div>
            {/* SỬ DỤNG COMPONENT DROPDOWN MỚI */}
            <SelectWithAddNew
              label="Danh mục"
              name="categoryId"
              value={productForm.categoryId}
              onChange={handleFormChange}
              options={categories.map(c => ({ value: c.categoryId, label: c.name }))}
              onAddNew={() => {
                console.log("Mở popup thêm Category!");
                setShowAddCategoryModal(true);
              }}
              required
            />
            <SelectWithAddNew
              label="Nhà cung cấp"
              name="supplierId"
              value={productForm.supplierId}
              onChange={handleFormChange}
              options={suppliers.map(s => ({ value: s.supplierId, label: s.name }))}
              onAddNew={() => {
                console.log("Mở popup thêm Supplier!");
                setShowAddSupplierModal(true);
              }}
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
          <select
            multiple
            className="mt-1 block w-full p-2 border rounded-md h-40"
            value={relatedProductIds.map(String)}
            onChange={(e) => {
              const opts = Array.from(e.target.selectedOptions).map((o) => Number(o.value));
              setRelatedProductIds(opts);
            }}
          >
            <Select
            isMulti
            options={(allProductsForSelect?.productsPage.content ?? [])
              .filter((p) => !isEditMode || p.productId !== currentProductIdNum)
              .map((p) => ({ value: p.productId, label: p.name }))}
            value={(allProductsForSelect?.productsPage.content ?? [])
              .filter((p) => relatedProductIds.includes(p.productId))
              .map((p) => ({ value: p.productId, label: p.name }))}
            onChange={(vals: MultiValue<OptionType>) => {
              const selected = vals;
              setRelatedProductIds(selected.map((v) => v.value));
            }}
            placeholder="Chọn sản phẩm liên quan..."
            classNamePrefix="react-select"
          />
          </select>
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
                onAddNew={() => {
                  console.log("Mở popup thêm Kho!");
                  setShowAddInventoryModal(true);
                }}
                required
              />
              <div>
                <label htmlFor="stockNumber">Số lượng ban đầu (*)</label>
                <input type="number" name="stockNumber" id="stockNumber" value={inventoryForm.stockNumber} required onChange={handleInventoryFormChange} className="mt-1 block w-full p-2 border rounded-md" />
              </div>
              <div>
                <label htmlFor="stockLevel">Tình trạng tồn kho (*)</label>
                <select name="stockLevel" id="stockLevel" value={inventoryForm.stockLevel} required onChange={handleInventoryFormChange} className="mt-1 block w-full p-2 border rounded-md">
                  <option value="medium">Trung bình</option>
                  <option value="high">Cao</option>
                  <option value="low">Thấp</option>
                </select>
              </div>
            </div>
          </fieldset>
        )}

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
          <button type="submit" disabled={addProductMutation.isPending} className="bg-green-600 text-white font-bold py-2 px-6 rounded-md hover:bg-green-700 disabled:bg-gray-400">
            {addProductMutation.isPending ? 'Đang xử lý...' : 'Lưu Sản Phẩm'}
          </button>
        </div>
      </form>



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