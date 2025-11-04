'use client';

import React from 'react';
import { motion } from 'framer-motion';
import { X } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { CategoryResponse } from '@/lib/types/api';

interface ProductFiltersProps {
  categories: CategoryResponse[];
  selectedCategory: number | null;
  onCategoryChange: (categoryId: number | null) => void;
  priceRange: [number, number];
  onPriceRangeChange: (range: [number, number]) => void;
  sortBy: string;
  onSortChange: (sort: string) => void;
  onReset: () => void;
}

export const ProductFilters: React.FC<ProductFiltersProps> = ({
  categories,
  selectedCategory,
  onCategoryChange,
  priceRange,
  onPriceRangeChange,
  sortBy,
  onSortChange,
  onReset,
}) => {
  return (
    <div className="space-y-6">
      {/* Categories */}
      <div>
        <h3 className="font-semibold text-main-text mb-3">Categories</h3>
        <div className="space-y-2">
          <button
            onClick={() => onCategoryChange(null)}
            className={`w-full text-left px-3 py-2 rounded-lg transition-colors ${
              selectedCategory === null
                ? 'bg-main-text text-white'
                : 'text-semi-text hover:bg-gray-100'
            }`}
          >
            All Categories
          </button>
          {categories.map((category) => (
            <button
              key={category.id}
              onClick={() => onCategoryChange(category.id)}
              className={`w-full text-left px-3 py-2 rounded-lg transition-colors ${
                selectedCategory === category.id
                  ? 'bg-main-text text-white'
                  : 'text-semi-text hover:bg-gray-100'
              }`}
            >
              {category.name}
            </button>
          ))}
        </div>
      </div>

      {/* Price Range */}
      <div>
        <h3 className="font-semibold text-main-text mb-3">Price Range</h3>
        <div className="space-y-3">
          <input
            type="range"
            min="0"
            max="10000000"
            step="100000"
            value={priceRange[1]}
            onChange={(e) => onPriceRangeChange([0, Number(e.target.value)])}
            className="w-full"
          />
          <div className="flex justify-between text-sm text-semi-text">
            <span>0 VND</span>
            <span>{priceRange[1].toLocaleString()} VND</span>
          </div>
        </div>
      </div>

      {/* Sort */}
      <div>
        <h3 className="font-semibold text-main-text mb-3">Sort By</h3>
        <select
          value={sortBy}
          onChange={(e) => onSortChange(e.target.value)}
          className="w-full input-field"
        >
          <option value="createdAt">Newest</option>
          <option value="price">Price: Low to High</option>
          <option value="price-desc">Price: High to Low</option>
          <option value="name">Name: A-Z</option>
          <option value="rating">Highest Rated</option>
        </select>
      </div>

      {/* Reset */}
      <Button variant="outline" className="w-full" onClick={onReset}>
        <X className="w-4 h-4 mr-2" />
        Reset Filters
      </Button>
    </div>
  );
};
