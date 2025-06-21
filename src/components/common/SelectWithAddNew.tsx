// File: src/components/Common/SelectWithAddNew.tsx

import React from 'react';

// Định nghĩa kiểu dữ liệu cho các lựa chọn trong dropdown
type Option = {
  value: string | number;
  label: string;
};

// Định nghĩa các props mà component sẽ nhận vào
type SelectWithAddNewProps = {
  label: string;
  name: string;
  value: string | number | undefined;
  onChange: (e: React.ChangeEvent<HTMLSelectElement>) => void;
  options: Option[];
  onAddNew: () => void; // Hàm sẽ được gọi khi nhấn "Thêm mới"
  isLoading?: boolean;
  required?: boolean;
  className?: string;
};

const SelectWithAddNew: React.FC<SelectWithAddNewProps> = ({
  label,
  name,
  value,
  onChange,
  options,
  onAddNew,
  isLoading = false,
  required = false,
  className = 'mt-1 block w-full p-2 border rounded-md',
}) => {

  // Một hàm xử lý onChange nội bộ
  const internalOnChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    // Nếu người dùng chọn vào dòng "add_new"
    if (e.target.value === 'add_new') {
      // Gọi hàm onAddNew mà component cha truyền vào (ví dụ: để mở popup)
      onAddNew();
    } else {
      // Nếu không, gọi hàm onChange bình thường
      onChange(e);
    }
  };

  return (
    <div>
      <label htmlFor={name} className="block text-sm font-medium text-gray-700">
        {label} {required && '(*)'}
      </label>
      <select
        name={name}
        id={name}
        value={value || ''}
        required={required}
        onChange={internalOnChange}
        className={className}
        disabled={isLoading}
      >
        <option value="">{isLoading ? 'Đang tải...' : `Chọn ${label.toLowerCase()}`}</option>
        
        {/* Render danh sách các lựa chọn được truyền vào */}
        {options.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
        
        {/* Đây là lựa chọn đặc biệt để thêm mới */}
        <option value="add_new" className="font-bold text-green-600 bg-gray-50">
          + Thêm {label.toLowerCase()} mới...
        </option>
      </select>
    </div>
  );
};

export default SelectWithAddNew;