/** @type {import('tailwindcss').Config} */
// tailwind.config.js
export const content = [
  "./src/**/*.{js,jsx,ts,tsx}", // Đảm bảo đường dẫn này khớp với các tệp của bạn
];
export const theme = {
  extend: {
    backgroundImage: {
      'login-background': "url('./assets/images/imgBackground.png')", // Thay đổi đường dẫn này
    },
    // Nếu bạn muốn dùng hiệu ứng bóng đổ tùy chỉnh như shadow-3xl đã nói ở trên
    boxShadow: {
      '3xl': '0 35px 60px -15px rgba(0, 0, 0, 0.3)', // Ví dụ cho shadow-3xl
    },
    keyframes: {
      'fade-in-down': {
        '0%': {
          opacity: '0',
          transform: 'translateY(-10px)'
        },
        '100%': {
          opacity: '1',
          transform: 'translateY(0)'
        },
      },
      blob: {
        '0%': {
          transform: 'translate(0px, 0px) scale(1)'
        },
        '33%': {
          transform: 'translate(30px, -50px) scale(1.1)'
        },
        '66%': {
          transform: 'translate(-20px, 20px) scale(0.9)'
        },
        '100%': {
          transform: 'translate(0px, 0px) scale(1)'
        },
      },
      'fade-in': {
        '0%': { opacity: '0' },
        '100%': { opacity: '1' },
      }
    },
    animation: {
      'fade-in-down': 'fade-in-down 0.6s ease-out',
      'blob': 'blob 7s infinite',
      'fade-in': 'fade-in 0.5s ease-out',
      'scale-in': 'scaleIn 0.3s ease-out forwards',
    }
  },
};
export const plugins = [];
