// components/ImageUploader.tsx
'use client';

import { useState, ChangeEvent, FormEvent, useEffect } from 'react';
// Sửa lại import để dùng hàm upload nhiều ảnh
import { uploadMultipleImages } from '../services/storageService';

export default function ImageUploader() {
  // 1. Cập nhật State để xử lý mảng
  const [files, setFiles] = useState<File[]>([]);
  const [imagePreviews, setImagePreviews] = useState<string[]>([]);
  const [isUploading, setIsUploading] = useState(false);
  const [message, setMessage] = useState('');
  const [uploadedImageUrls, setUploadedImageUrls] = useState<string[]>([]);

  // Mentor's Advice: Dọn dẹp các object URL để tránh memory leak
  useEffect(() => {
    // Trả về một cleanup function
    return () => {
      imagePreviews.forEach(url => URL.revokeObjectURL(url));
    };
  }, [imagePreviews]);

  // 2. Cập nhật hàm xử lý khi người dùng chọn file
  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const selectedFiles = Array.from(e.target.files);
      setFiles(selectedFiles);
      setMessage('');
      setUploadedImageUrls([]); // Reset ảnh đã upload trước đó

      // Tạo các URL preview cho các ảnh vừa chọn
      const previewUrls = selectedFiles.map(file => URL.createObjectURL(file));
      setImagePreviews(previewUrls);
    }
  };

  // 3. Cập nhật hàm submit để gọi đúng hàm upload
  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (files.length === 0) {
      setMessage('Vui lòng chọn ít nhất một file ảnh.');
      return;
    }

    setIsUploading(true);
    setMessage(`Đang tải lên ${files.length} ảnh...`);

    try {
      // Gọi hàm uploadMultipleImages
      const publicUrls = await uploadMultipleImages(files, 'public'); // Lưu vào thư mục 'public'
      
      setMessage('Tải ảnh lên thành công!');
      setUploadedImageUrls(publicUrls);
      setFiles([]); // Xóa file đã chọn sau khi upload thành công
      setImagePreviews([]); // Xóa preview
      console.log('URLs các ảnh đã tải lên:', publicUrls);

    } catch (error) {
      // Xử lý lỗi
      const errorMessage = error instanceof Error ? error.message : 'Đã có lỗi không xác định xảy ra.';
      setMessage(`Lỗi: ${errorMessage}`);
      console.error(error);
    } finally {
      setIsUploading(false);
    }
  };

  return (
    <div className="max-w-2xl p-6 mx-auto mt-10 bg-white border border-gray-200 rounded-lg shadow-md dark:bg-gray-800 dark:border-gray-700">
      <h2 className="mb-4 text-2xl font-bold text-gray-900 dark:text-white">Upload Nhiều Hình Ảnh</h2>
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label htmlFor="image-upload" className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">
            Chọn ảnh (có thể chọn nhiều)
          </label>
          <input
            type="file"
            id="image-upload"
            accept="image/png, image/jpeg, image/gif"
            onChange={handleFileChange}
            className="block w-full text-sm text-gray-900 border border-gray-300 rounded-lg cursor-pointer bg-gray-50 dark:text-gray-400 focus:outline-none dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400"
            disabled={isUploading}
            multiple // 4. Thêm thuộc tính 'multiple'
          />
        </div>
        <button
          type="submit"
          disabled={isUploading || files.length === 0}
          className="w-full px-4 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed focus:ring-4 focus:outline-none focus:ring-blue-300 dark:bg-blue-500 dark:hover:bg-blue-600 dark:focus:ring-blue-800"
        >
          {isUploading ? 'Đang tải...' : `Upload ${files.length} ảnh`}
        </button>
      </form>

      {message && <p className="mt-4 text-sm font-semibold text-center text-gray-600 dark:text-gray-300">{message}</p>}

      {/* 5. Hiển thị các ảnh preview đã chọn */}
      {imagePreviews.length > 0 && (
        <div className="mt-6">
          <h3 className="mb-2 font-semibold text-gray-900 dark:text-white">Ảnh đã chọn:</h3>
          <div className="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-5 gap-4">
            {imagePreviews.map((src, index) => (
              <img key={index} src={src} alt={`Preview ${index}`} className="object-cover w-24 h-24 rounded-lg shadow-md" />
            ))}
          </div>
        </div>
      )}
      
      {/* 6. Hiển thị các ảnh đã upload thành công */}
      {uploadedImageUrls.length > 0 && (
        <div className="mt-6">
          <h3 className="mb-2 font-semibold text-gray-900 dark:text-white">Ảnh đã tải lên thành công:</h3>
          <div className="flex flex-col gap-y-2">
            {uploadedImageUrls.map((url, index) => (
              <div key={index} className="flex items-center gap-x-2">
                <img src={url} alt={`Uploaded ${index}`} className="object-cover w-12 h-12 rounded-md" />
                <input 
                  type="text" 
                  readOnly 
                  value={url} 
                  className="w-full p-2 text-sm text-gray-900 border border-gray-300 rounded-md bg-gray-50 dark:bg-gray-700 dark:text-gray-300 dark:border-gray-600"
                />
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}