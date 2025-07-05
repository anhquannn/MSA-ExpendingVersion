// File: src/components/Common/Modal.tsx

import React from 'react';

type ModalProps = {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children?: React.ReactNode; // `children` là optional để có thể hiển thị modal trống khi cần
};

const Modal: React.FC<ModalProps> = ({ isOpen, onClose, title, children }) => {
  if (!isOpen) {
    return null;
  }

  // Ngăn việc nhấn vào nội dung modal làm đóng modal
  const handleContentClick = (e: React.MouseEvent) => {
    e.stopPropagation();
  };

  return (
    // Lớp phủ màu đen
    <div
      onClick={onClose}
      className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50 transition-opacity"
    >
      {/* Khung chứa nội dung modal */}
      <div
        onClick={handleContentClick}
        className="bg-white p-6 rounded-lg shadow-xl w-full max-w-lg animate-fade-in-up"
      >
        {/* Header của modal */}
        <div className="flex justify-between items-center border-b pb-3 mb-4">
          <h3 className="text-xl font-semibold text-gray-800">{title}</h3>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-800 text-2xl"
          >
            &times; {/* Dấu "X" */}
          </button>
        </div>
        
        {/* Nội dung chính của modal (chính là form của chúng ta) */}
        <div>
          {children}
        </div>
      </div>
    </div>
  );
};

export default Modal;