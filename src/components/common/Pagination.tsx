import React from 'react';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

/**
 * Generic pagination component with Previous / Next buttons and clickable page numbers.
 * Designed for simple datasets where total pages are not too large.
 *
 * Usage:
 * <Pagination currentPage={page} totalPages={total} onPageChange={goToPage} />
 */
const Pagination: React.FC<PaginationProps> = ({ currentPage, totalPages, onPageChange }) => {
  if (totalPages <= 1) return null; // No pagination needed

  const pageNumbers: number[] = [];
  for (let i = 1; i <= totalPages; i += 1) {
    pageNumbers.push(i);
  }

  const buttonClass = (active: boolean) =>
    `mx-1 px-3 py-1 border rounded-md ${active ? 'bg-blue-600 text-white' : 'bg-white hover:bg-gray-100'} transition disabled:opacity-50`;

  return (
    <nav className="flex items-center">
      {/* Previous */}
      <button
        className={buttonClass(false)}
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 1}
      >
        Trước
      </button>

      {/* Page Numbers */}
      {pageNumbers.map((num) => (
        <button
          key={num}
          className={buttonClass(num === currentPage)}
          onClick={() => onPageChange(num)}
        >
          {num}
        </button>
      ))}

      {/* Next */}
      <button
        className={buttonClass(false)}
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages}
      >
        Sau
      </button>
    </nav>
  );
};

export default Pagination;
