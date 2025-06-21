// File: src/pages/Dashboard/CategoryManagementPage.tsx

import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { categoryService, Category, CreateCategoryPayload, UpdateCategoryPayload, CategoryPagingParams } from '../../services/categoryService';
import Modal from '../../components/common/Modal'; // Giả sử đường dẫn này đúng

// --- Custom Hook để Debounce ---
function useDebounce(value: string, delay: number) {
    const [debouncedValue, setDebouncedValue] = useState(value);
    useEffect(() => {
        const handler = setTimeout(() => { setDebouncedValue(value); }, delay);
        return () => clearTimeout(handler);
    }, [value, delay]);
    return debouncedValue;
}

// --- Component Form cho việc Thêm/Sửa Category trong Modal ---
const CategoryForm = ({ onSuccess, initialData }: {
    onSuccess: () => void;
    initialData?: Category | null;
}) => {
    const queryClient = useQueryClient();
    const [formData, setFormData] = useState({
        name: initialData?.name || '',
        description: initialData?.description || '',
        parentCategoryId: initialData?.parentCategory?.categoryId,
    });

    const { data: parentCategories = [], isLoading: isLoadingCategories } = useQuery({
        queryKey: ['allCategoriesForSelect'], // Dùng key riêng cho dropdown
        queryFn: () => categoryService.getCategories({ pageSize: 999 }).then(res => res.content)
    });

    //   const mutation = useMutation({
    //     mutationFn: (payload: CreateCategoryPayload | UpdateCategoryPayload) => {
    //       if (initialData?.categoryId) {
    //         // Payload cho update chỉ cần name và description
    //         const updatePayload: UpdateCategoryPayload = { 
    //             name: (payload as CreateCategoryPayload).name, 
    //             description: (payload as CreateCategoryPayload).description 
    //         };
    //         return categoryService.updateCategory(initialData.categoryId, updatePayload);
    //       }
    //       return categoryService.createCategory(payload as CreateCategoryPayload);
    //     },
    //     onSuccess: () => {
    //       alert(initialData ? 'Cập nhật thành công!' : 'Thêm thành công!');
    //       // Làm mới lại cả 2 query: danh sách có phân trang và danh sách cho dropdown
    //       queryClient.invalidateQueries({ queryKey: ['categories'] });
    //       queryClient.invalidateQueries({ queryKey: ['allCategoriesForSelect'] });
    //       onSuccess();
    //     },
    //     onError: (err: Error) => alert(`Lỗi: ${err.message}`),
    //   });

    const mutation = useMutation<
        Category, // Kiểu dữ liệu trả về khi thành công
        Error,    // Kiểu của lỗi
        CreateCategoryPayload | UpdateCategoryPayload // Kiểu của payload gửi đi
    >({
        mutationFn: (payload) => {
            // Logic if/else giờ đây hoàn toàn hợp lệ vì cả 2 hàm đều trả về Promise<Category>
            if (initialData?.categoryId) {
                const updatePayload: UpdateCategoryPayload = {
                    name: (payload as CreateCategoryPayload).name,
                    description: (payload as CreateCategoryPayload).description
                };
                return categoryService.updateCategory(initialData.categoryId, updatePayload);
            }
            return categoryService.createCategory(payload as CreateCategoryPayload);
        },
        // `data` ở đây giờ chính là object `Category`
        onSuccess: (data) => {
            alert(initialData ? `Cập nhật '${data.name}' thành công!` : `Thêm '${data.name}' thành công!`);
            queryClient.invalidateQueries({ queryKey: ['categories'] });
            queryClient.invalidateQueries({ queryKey: ['allCategoriesForSelect'] });
            onSuccess();
        },
        onError: (err: Error) => alert(`Lỗi: ${err.message}`),
    });

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (!formData.name) {
            alert("Tên danh mục là bắt buộc.");
            return;
        }
        mutation.mutate(formData);
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            <input name="name" value={formData.name} onChange={e => setFormData(p => ({ ...p, name: e.target.value }))} placeholder="Tên loại sản phẩm (*)" required className="w-full p-2 border rounded" />
            <textarea name="description" value={formData.description} onChange={e => setFormData(p => ({ ...p, description: e.target.value }))} placeholder="Mô tả" className="w-full p-2 border rounded" />
            <select name="parentCategoryId" value={formData.parentCategoryId || ''} disabled={isLoadingCategories} onChange={e => setFormData(p => ({ ...p, parentCategoryId: e.target.value ? Number(e.target.value) : undefined }))} className="w-full p-2 border rounded">
                <option value="">{isLoadingCategories ? 'Đang tải...' : 'Không có danh mục cha'}</option>
                {parentCategories.filter(cat => cat.categoryId !== initialData?.categoryId).map(cat => ( // Lọc để không thể chọn chính nó làm cha
                    <option key={cat.categoryId} value={cat.categoryId}>{cat.name}</option>
                ))}
            </select>
            <div className="flex justify-end">
                <button type="submit" disabled={mutation.isPending} className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 disabled:bg-gray-400">
                    {mutation.isPending ? 'Đang lưu...' : 'Lưu'}
                </button>
            </div>
        </form>
    );
};


const CategoryManagementPage: React.FC = () => {
    const queryClient = useQueryClient();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingCategory, setEditingCategory] = useState<Category | null>(null);

    // State cho bộ lọc và phân trang
    const [filters, setFilters] = useState<CategoryPagingParams>({
        page: 1,
        pageSize: 10,
        name: '', // Thêm `name` để làm keyword tìm kiếm
    });
    const debouncedSearchTerm = useDebounce(filters.name || '', 500);

    const { data: pagedData, isLoading, isError, error } = useQuery({
        queryKey: ['categories', { ...filters, name: debouncedSearchTerm }],
        queryFn: () => categoryService.getCategories({ ...filters, name: debouncedSearchTerm }),
        placeholderData: keepPreviousData,
    });

    const categories = pagedData?.content || [];
    const totalPages = pagedData?.totalPages || 1;

    const deleteMutation = useMutation({
        mutationFn: (id: number) => categoryService.deleteCategory(id),
        onSuccess: () => {
            alert('Xóa thành công!');
            queryClient.invalidateQueries({ queryKey: ['categories'] });
        },
        onError: (err: Error) => alert(`Lỗi: ${err.message}`),
    });

    const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFilters(prev => ({ ...prev, name: e.target.value, page: 1 }));
    };

    const handlePageChange = (newPage: number) => {
        setFilters(prev => ({ ...prev, page: newPage }));
    };

    const handleOpenAddModal = () => {
        setEditingCategory(null);
        setIsModalOpen(true);
    };

    const handleOpenEditModal = (category: Category) => {
        setEditingCategory(category);
        setIsModalOpen(true);
    };

    const handleDelete = (category: Category) => {
        if (window.confirm(`Bạn có chắc muốn xóa loại sản phẩm "${category.name}"?`)) {
            deleteMutation.mutate(category.categoryId);
        }
    };

    return (
        <div className="bg-white p-6 rounded-lg shadow-md">
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-semibold">Quản lý Loại Sản Phẩm</h2>
                <button onClick={handleOpenAddModal} className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">Thêm Mới</button>
            </div>

            <div className="mb-4">
                <input
                    type="text"
                    placeholder="Tìm theo tên loại sản phẩm..."
                    value={filters.name}
                    onChange={handleFilterChange}
                    className="p-2 border rounded-md w-full md:w-1/3"
                />
            </div>

            {isLoading && <p className="text-center py-4">Đang tải...</p>}
            {isError && <p className="text-center py-4 text-red-500">Lỗi: {(error as Error).message}</p>}

            <div className="overflow-x-auto">
                <table className="min-w-full bg-white border">
                    <thead className="bg-gray-100">
                        <tr>
                            <th className="py-3 px-6 text-left">ID</th>
                            <th className="py-3 px-6 text-left">Tên Loại</th>
                            <th className="py-3 px-6 text-left">Mô tả</th>
                            <th className="py-3 px-6 text-left">Danh mục cha</th>
                            <th className="py-3 px-6 text-center">Hành Động</th>
                        </tr>
                    </thead>
                    <tbody className="text-gray-600 text-sm">
                        {categories.map(cat => (
                            <tr key={cat.categoryId} className="border-b hover:bg-gray-50">
                                <td className="py-3 px-6">{cat.categoryId}</td>
                                <td className="py-3 px-6 font-medium">{cat.name}</td>
                                <td className="py-3 px-6">{cat.description}</td>
                                <td className="py-3 px-6">{cat.parentCategory?.name || 'N/A'}</td>
                                <td className="py-3 px-6 text-center">
                                    <button onClick={() => handleOpenEditModal(cat)} className="text-yellow-600 hover:underline mr-4">Sửa</button>
                                    <button onClick={() => handleDelete(cat)} disabled={deleteMutation.isPending} className="text-red-600 hover:underline disabled:text-gray-400">
                                        {deleteMutation.isPending ? '...' : 'Xóa'}
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Phân trang */}
            <div className="flex justify-between items-center mt-6">
                <p className="text-sm">Trang {pagedData?.number ? pagedData.number + 1 : 1} trên {totalPages}</p>
                <div className="flex space-x-2">
                    <button onClick={() => handlePageChange(filters.page! - 1)} disabled={pagedData?.number === 0 || isLoading} className="px-4 py-2 border rounded disabled:opacity-50">Trước</button>
                    <button onClick={() => handlePageChange(filters.page! + 1)} disabled={((pagedData?.number ?? 0) + 1 >= totalPages) || isLoading} className="px-4 py-2 border rounded disabled:opacity-50">Sau</button>
                </div>
            </div>

            <Modal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                title={editingCategory ? 'Sửa Loại Sản Phẩm' : 'Thêm Loại Sản Phẩm'}
            >
                <CategoryForm
                    onSuccess={() => setIsModalOpen(false)}
                    initialData={editingCategory}
                />
            </Modal>
        </div>
    );
};

export default CategoryManagementPage;