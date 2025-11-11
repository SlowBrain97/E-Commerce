'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { motion } from 'framer-motion';
import { categoriesApi } from '@/lib/api/categories';
import { CategoryResponse } from '@/lib/types/api';
import Image from 'next/image';

export default function CategoriesPage() {
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await categoriesApi.getAllCategoriesInHierarchy();
      if (response.success) {
        setCategories(response.data);
      }
    } catch (error) {
      console.error('Failed to fetch categories:', error);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className="container-custom py-12">
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {[...Array(8)].map((_, i) => (
            <div key={i} className="card overflow-hidden animate-pulse">
              <div className="aspect-square bg-gray-200" />
              <div className="p-4">
                <div className="h-4 bg-gray-200 rounded w-3/4" />
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  const mainCategories = categories.filter(cat => !cat.parentId);

  return (
    <div className="container-custom py-12">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-center mb-12"
      >
        <h1 className="text-3xl md:text-4xl font-bold text-main-text mb-4">
          Shop by Category
        </h1>
        <p className="text-semi-text text-lg">
          Explore our wide range of footwear categories
        </p>
      </motion.div>

      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        {mainCategories.map((category, index) => (
          <motion.div
            key={category.id}
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: index * 0.05 }}
            whileHover={{ y: -8 }}
          >
            <Link href={`/products?category=${category.id}`}>
              <div className="card overflow-hidden cursor-pointer group h-full">
                 <div className="aspect-square bg-gray-100 overflow-hidden">
                                        <Image 
                                          src={category.imageUrl || '/placeholder-product.jpg'}
                                          alt={category.name}
                                          width={300}
                                          height={300}
                                          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                                        />
                                  </div>
        
                <div className="p-6">
                  <h3 className="font-bold text-main-text text-lg mb-2 group-hover:text-semi-text transition-colors">
                    {category.name}
                  </h3>
                  {category.description && (
                    <p className="text-sm text-semi-text line-clamp-2">
                      {category.description}
                    </p>
                  )}
                  {category.subcategoryCount > 0 && (
                    <p className="text-xs text-semi-text mt-2">
                      {category.subcategoryCount} subcategories
                    </p>
                  )}
                </div>
              </div>
            </Link>
          </motion.div>
        ))}
      </div>
    </div>
  );
}
