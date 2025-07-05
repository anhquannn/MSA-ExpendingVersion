import React from 'react';
import { TrendingUp, TrendingDown, DollarSign, ShoppingCart } from 'lucide-react';

interface StatCardProps {
  title: string;
  value: string;
  change?: number;
  icon: React.ReactNode;
  color?: 'blue' | 'green' | 'purple' | 'red' | 'yellow';
}

const colorMap: Record<NonNullable<StatCardProps['color']>, {iconBg: string; iconColor: string}> = {
  blue: { iconBg: 'bg-blue-100', iconColor: 'text-blue-600'},
  green: { iconBg: 'bg-green-100', iconColor: 'text-green-600'},
  purple: { iconBg: 'bg-purple-100', iconColor: 'text-purple-600'},
  red: { iconBg: 'bg-red-100', iconColor: 'text-red-600'},
  yellow: { iconBg: 'bg-yellow-100', iconColor: 'text-yellow-600'},
};

const StatCard: React.FC<StatCardProps> = ({ title, value, change = 0, icon, color = 'blue' }) => {
  const isPositive = change >= 0;
  
  const { iconBg, iconColor } = colorMap[color];

  return (
    <div className="bg-white rounded-xl shadow-md p-6 hover:shadow-lg transition-shadow">
      <div className="flex justify-between items-start">
        <div>
          <p className="text-sm font-medium text-gray-500">{title}</p>
          <p className="text-2xl font-bold mt-1">{value}</p>
          {change !== undefined && (
          <div className={`flex items-center mt-2 ${isPositive ? 'text-green-500' : 'text-red-500'}`}>
            {isPositive ? <TrendingUp size={16} /> : <TrendingDown size={16} />}
            <span className="ml-1 text-sm font-medium">{Math.abs(change)}%</span>
            <span className="text-xs text-gray-500 ml-1">so với tháng trước</span>
          </div>
          )}
        </div>
        {/* icon */}
        <div className={`p-3 rounded-lg ${iconBg} ${iconColor}`}>
          {icon}
        </div>
      </div>
    </div>
  );
};

export default StatCard;
