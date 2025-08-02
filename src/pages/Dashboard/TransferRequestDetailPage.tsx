import React, { useState, useCallback } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { transferService } from '../../services/transferService';
import { transferItemService, TransferResponseItem } from '../../services/transferItemService';
import { toast } from 'react-toastify';
import { Edit, Check, X, ArrowLeft, Trash2 } from 'lucide-react';

type StatusType = 'PENDING' | 'APPROVED' | 'REJECTED' | 'COMPLETED' | 'IN_PROGRESS';

const statusClasses: Record<StatusType, string> = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  APPROVED: 'bg-green-100 text-green-800',
  REJECTED: 'bg-red-100 text-red-800',
  COMPLETED: 'bg-blue-100 text-blue-800',
  IN_PROGRESS: 'bg-purple-100 text-purple-800',
};

const StatusBadge: React.FC<{ status: StatusType }> = ({ status }) => (
  <span className={`px-2 py-1 text-xs font-medium rounded-full ${statusClasses[status]}`}>
    {status}
  </span>
);

interface TransferRequestDetailProps {}

interface TransferRequestDetail {
  transferRequestId: number;
  status: StatusType;
  note: string;
  createdAt: string;
  updatedAt: string | null;
  requesterResponse: {
    userId: number;
    fullName: string;
    email: string;
  };
  approverResponse: {
    userId: number;
    fullName: string;
    email: string;
  };
  fromInventoryResponse: {
    inventoryId: number;
    name: string;
    address: string;
  };
  toInventoryResponse: {
    inventoryId: number;
    name: string;
    address: string;
  };
  transferItems: TransferResponseItem[];
}

const TransferRequestDetailPage: React.FC<TransferRequestDetailProps> = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const requestId = id ? parseInt(id, 10) : 0;
  const isValidRequestId = !isNaN(requestId) && requestId > 0;

  // State for editing
  const [editingItem, setEditingItem] = useState<TransferResponseItem | null>(null);
  const [newQuantity, setNewQuantity] = useState<number>(0);

  // Fetch transfer request details
  const { data: transferData, isLoading: isLoadingRequest } = useQuery({
    queryKey: ['transferRequest', requestId],
    queryFn: () => transferService.getTransferRequestById(requestId),
    enabled: isValidRequestId,
  });

  // Fetch transfer items
  const { data: itemsData, isLoading: isLoadingItems } = useQuery({
    queryKey: ['transferItems', requestId],
    queryFn: () => 
      transferItemService.getTransferItems({
        transferRequestId: requestId,
        page: 1,
        pageSize: 10,
      }),
    enabled: isValidRequestId,
    select: (data) => data.content || [],
  });

  // Approve transfer request mutation
  const approveMutation = useMutation({
    mutationFn: () => transferService.approveTransferRequest(requestId),
    onSuccess: () => {
      toast.success('Đã phê duyệt yêu cầu chuyển hàng');
      queryClient.invalidateQueries({ queryKey: ['transferRequest', requestId] });
    },
    onError: (error: Error) => {
      console.error('Error approving transfer request:', error);
      toast.error('Có lỗi xảy ra khi phê duyệt yêu cầu');
    },
  });

  // Reject transfer request mutation
  const rejectMutation = useMutation({
    mutationFn: (reason: string) => transferService.rejectTransferRequest(requestId, reason),
    onSuccess: () => {
      toast.success('Đã từ chối yêu cầu chuyển hàng');
      queryClient.invalidateQueries({ queryKey: ['transferRequest', requestId] });
    },
    onError: (error: Error) => {
      console.error('Error rejecting transfer request:', error);
      toast.error('Có lỗi xảy ra khi từ chối yêu cầu');
    },
  });

  // Update transfer item quantity mutation
  const updateQuantityMutation = useMutation({
    mutationFn: (params: { itemId: number; quantity: number }) =>
      transferItemService.updateTransferItem(params.itemId, { 
        quantityRequested: editingItem?.quantityRequested || 0,
        quantityTransferred: params.quantity,
        productId: editingItem?.productResponse.productId!,
        transferRequestId: requestId,
      }),
    onSuccess: () => {
      toast.success('Đã cập nhật số lượng');
      setEditingItem(null);
      queryClient.invalidateQueries({ queryKey: ['transferItems', requestId] });
    },
    onError: (error: Error) => {
      console.error('Error updating transfer item quantity:', error);
      toast.error('Có lỗi xảy ra khi cập nhật số lượng');
    },
  });

  // Delete transfer item mutation
  const deleteItemMutation = useMutation({
    mutationFn: (itemId: number) => transferItemService.deleteTransferItem(itemId),
    onSuccess: () => {
      toast.success('Đã xóa sản phẩm khỏi yêu cầu');
      queryClient.invalidateQueries({ queryKey: ['transferItems', requestId] });
    },
    onError: (error: Error) => {
      console.error('Error deleting transfer item:', error);
      toast.error('Có lỗi xảy ra khi xóa sản phẩm');
    },
  });

  const handleStartEdit = (item: TransferResponseItem) => {
    setEditingItem(item);
    setNewQuantity(item.quantityTransferred || 0);
  };

  const handleSaveEdit = () => {
    if (editingItem) {
      updateQuantityMutation.mutate({
        itemId: editingItem.transferRequestItemId,
        quantity: newQuantity,
      });
    }
  };

  const handleDeleteItem = (itemId: number) => {
    if (window.confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi yêu cầu?')) {
      deleteItemMutation.mutate(itemId);
    }
  };

  const handleApprove = () => {
    if (window.confirm('Bạn có chắc chắn muốn phê duyệt yêu cầu này?')) {
      approveMutation.mutate();
    }
  };

  const handleReject = () => {
    const reason = window.prompt('Vui lòng nhập lý do từ chối:');
    if (reason) {
      rejectMutation.mutate(reason);
    } else if (reason !== null) {
      toast.warning('Vui lòng nhập lý do từ chối');
    }
  };

  if (isLoadingRequest || isLoadingItems) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (!transferData || !itemsData) {
    return (
      <div className="text-center py-10">
        <p>Không tìm thấy thông tin yêu cầu chuyển hàng</p>
        <Link 
          to="/dashboard/transfer-requests" 
          className="mt-4 inline-flex items-center text-blue-600 hover:text-blue-800"
        >
          <ArrowLeft className="w-4 h-4 mr-1" /> Quay lại danh sách
        </Link>
      </div>
    );
  }

  const normalizedStatus = (transferData.status || '').toUpperCase() as StatusType;

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="bg-white shadow rounded-lg p-6">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold text-gray-800">Chi tiết yêu cầu chuyển hàng</h1>
          <div className="flex space-x-2">
            <Link 
              to="/dashboard/transfer-requests" 
              className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
            >
              <ArrowLeft className="inline w-4 h-4 mr-1" /> Quay lại
            </Link>
            {normalizedStatus === 'PENDING' && (
              <>
                <button
                  onClick={handleApprove}
                  className="px-4 py-2 bg-green-600 text-white rounded-md text-sm font-medium hover:bg-green-700"
                  disabled={approveMutation.isPending}
                >
                  {approveMutation.isPending ? 'Đang xử lý...' : 'Phê duyệt'}
                </button>
                <button
                  onClick={handleReject}
                  className="px-4 py-2 bg-red-600 text-white rounded-md text-sm font-medium hover:bg-red-700"
                  disabled={rejectMutation.isPending}
                >
                  {rejectMutation.isPending ? 'Đang xử lý...' : 'Từ chối'}
                </button>
              </>
            )}
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <div>
            <h2 className="text-lg font-medium text-gray-900 mb-4">Thông tin chung</h2>
            <div className="space-y-2">
              <p><span className="font-medium">Mã yêu cầu:</span> {transferData.transferRequestId}</p>
              <p><span className="font-medium">Trạng thái:</span> <StatusBadge status={normalizedStatus} /></p>
              <p><span className="font-medium">Ngày tạo:</span> {new Date(transferData.createdAt).toLocaleString()}</p>
              <p><span className="font-medium">Ghi chú:</span> {transferData.note || 'Không có'}</p>
            </div>
          </div>
          <div>
            <h2 className="text-lg font-medium text-gray-900 mb-4">Thông tin kho</h2>
            <div className="space-y-2">
              <p><span className="font-medium">Từ kho:</span> {transferData.fromInventoryResponse.name}</p>
              <p><span className="font-medium">Đến kho:</span> {transferData.toInventoryResponse.name}</p>
              <p><span className="font-medium">Người yêu cầu:</span> {transferData.requesterResponse.fullName}</p>
              {transferData.approverResponse && (
                <p><span className="font-medium">Người duyệt:</span> {transferData.approverResponse.fullName}</p>
              )}
            </div>
          </div>
        </div>

        <div>
          <h2 className="text-lg font-medium text-gray-900 mb-4">Danh sách sản phẩm</h2>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Sản phẩm</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Số lượng yêu cầu</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Số lượng chuyển</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Thao tác</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {itemsData.map((item) => (
                  <tr key={item.transferRequestItemId}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <div>
                          <div className="text-sm font-medium text-gray-900">
                            {item.productResponse?.name || 'Không có tên'}
                          </div>
                          <div className="text-sm text-gray-500">
                            {item.productResponse?.netWeight}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">{item.quantityRequested}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {editingItem?.transferRequestItemId === item.transferRequestItemId ? (
                        <input
                          type="number"
                          value={newQuantity}
                          onChange={(e) => setNewQuantity(Number(e.target.value))}
                          min={0}
                          max={item.quantityRequested}
                          className="w-20 px-2 py-1 border rounded"
                        />
                      ) : (
                        <div className="text-sm text-gray-900">
                          {item.quantityTransferred || 0}
                        </div>
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      {editingItem?.transferRequestItemId === item.transferRequestItemId ? (
                        <div className="flex space-x-2">
                          <button
                            onClick={handleSaveEdit}
                            className="text-green-600 hover:text-green-900"
                            disabled={updateQuantityMutation.isPending}
                          >
                            <Check className="w-5 h-5" />
                          </button>
                          <button
                            onClick={() => setEditingItem(null)}
                            className="text-gray-600 hover:text-gray-900"
                          >
                            <X className="w-5 h-5" />
                          </button>
                        </div>
                      ) : (
                        normalizedStatus === 'PENDING' ? (
                          <div className="flex space-x-2">
                            <button
                              onClick={() => handleStartEdit(item)}
                              className="text-blue-600 hover:text-blue-900"
                            >
                              <Edit className="w-5 h-5" />
                            </button>
                            <button
                              onClick={() => handleDeleteItem(item.transferRequestItemId)}
                              className="text-red-600 hover:text-red-900"
                              disabled={deleteItemMutation.isPending}
                            >
                              <Trash2 className="w-5 h-5" />
                            </button>
                          </div>
                        ) : (
                          <span className="text-gray-400">—</span>
                        )
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TransferRequestDetailPage;
