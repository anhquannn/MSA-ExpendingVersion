// src/components/common/Input/Input.tsx
import React from 'react';
import './css/Input.css';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
}

const Input: React.FC<InputProps> = ({ label, ...props }) => {
  return (
    <div className="input-wrapper">
      {label && <label>{label}</label>}
      <input className="custom-input" {...props} />
    </div>
  );
};

export default Input;

