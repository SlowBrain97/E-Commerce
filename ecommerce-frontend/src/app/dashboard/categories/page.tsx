'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { 
  Plus, 
  Edit2, 
  Trash2, 
  FolderTree,
  Eye,
  ChevronRight,
  ChevronDown
} from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Card, CardHeader, CardContent } from '@/components/ui/Card';
import { categoriesApi } from '@/lib/api/categories';
import { CategoryResponse } from '@/lib/types/api';
import { toast } from 'react-hot-toast';

export default function DashboardCategoriesPage() {
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [expandedCategories, setExpandedCategories] = useState<Set<number>>(new Set());

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      setIsLoading(true);
      const response = await categoriesApi.getAllCategoriesInHierarchy();
      if (response.success) {
        setCategories(response.data);
      }
    } catch (error) {
      console.error('Failed to fetch categories:', error);
      toast.error('Không thể tải danh mục');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (categoryId: number) => {
    if (!confirm('Bạn có chắc muốn xóa danh mục này?')) return;

    try {
      await categoriesApi.deleteCategory(categoryId);
      toast.success('Đã xóa danh mục');
      fetchCategories();
    } catch (error) {
      console.error('Failed to delete category:', error);
      toast.error('Không thể xóa danh mục');
    }
  };

  const toggleExpand = (categoryId: number) => {
    const newExpanded = new Set(expandedCategories);
    if (newExpanded.has(categoryId)) {
      newExpanded.delete(categoryId);
    } else {
      newExpanded.add(categoryId);
    }
    setExpandedCategories(newExpanded);
  };

  const renderCategory = (category: CategoryResponse, level: number = 0) => {
    const hasChildren = category.subcategoryCount > 0;
    const isExpanded = expandedCategories.has(category.id);

    return (
      <div key={category.id}>
        <div
          className="flex items-center gap-4 p-4 hover:bg-gray-50 transition-colors"
          style={{ paddingLeft: `${level * 32 + 16}px` }}
        >
          {/* Expand/Collapse Button */}
          <button
            onClick={() => toggleExpand(category.id)}
            className={`p-1 rounded hover:bg-gray-200 transition-colors ${
              !hasChildren ? 'invisible' : ''
            }`}
          >
            {isExpanded ? (
              <ChevronDown className="w-4 h-4 text-[#6f6e72]" />
            ) : (
              <ChevronRight className="w-4 h-4 text-[#6f6e72]" />
            )}
          </button>

          {/* Icon */}
          <div className="w-12 h-12 rounded-lg bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center text-2xl flex-shrink-0">
            {category.icon || '📁'}
          </div>

          {/* Category Info */}
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-3 mb-1">
              <h3 className="font-semibold text-[#1d1d1f]">
                {category.name}
              </h3>
              {category.isFeatured && (
                <Badge variant="info">Nổi bật</Badge>
              )}
              <Badge variant={category.isActive ? 'success' : 'default'}>
                {category.isActive ? 'Hoạt động' : 'Ẩn'}
              </Badge>
            </div>
            {category.description && (
              <p className="text-sm text-[#6f6e72] line-clamp-1">
                {category.description}
              </p>
            )}
            <div className="flex items-center gap-4 mt-1 text-xs text-[#6f6e72]">
              {hasChildren && (
                <span>{category.subcategoryCount} danh mục con</span>
              )}
            </div>
          </div>

          {/* Actions */}
          <div className="flex items-center gap-2">
            <Button variant="ghost" size="sm">
              <Edit2 className="w-4 h-4" />
            </Button>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => handleDelete(category.id)}
              className="text-red-600 hover:text-red-700"
            >
              <Trash2 className="w-4 h-4" />
            </Button>
          </div>
        </div>
      </div>
    );
  };

  if (isLoading) {
    return (
      <div className="container-custom py-12">
        <div className="animate-pulse space-y-4">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="card p-6">
              <div className="flex gap-4">
                <div className="w-12 h-12 bg-gray-200 rounded" />
                <div className="flex-1 space-y-2">
                  <div className="h-4 bg-gray-200 rounded w-1/3" />
                  <div className="h-4 bg-gray-200 rounded w-1/2" />
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  const mainCategories = categories.filter(cat => !cat.parentId);
  const totalCategories = categories.length;
  const activeCategories = categories.filter(cat => cat.isActive).length;
  const featuredCategories = categories.filter(cat => cat.isFeatured).length;

  return (
    <div className="container-custom py-12">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold text-[#1d1d1f] mb-2">
            Quản Lý Danh Mục
          </h1>
          <p className="text-[#6f6e72]">
            Quản lý cấu trúc danh mục sản phẩm
          </p>
        </div>
        <Button size="lg" className="group">
          <Plus className="w-5 h-5 mr-2" />
          Thêm Danh Mục
        </Button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        {[
          {
            label: 'Tổng Danh Mục',
            value: totalCategories,
            color: 'text-blue-600',
            bgColor: 'bg-blue-100',
          },
          {
            label: 'Danh Mục Chính',
            value: mainCategories.length,
            color: 'text-purple-600',
            bgColor: 'bg-purple-100',
          },
          {
            label: 'Đang Hoạt Động',
            value: activeCategories,
            color: 'text-green-600',
            bgColor: 'bg-green-100',
          },
          {
            label: 'Nổi Bật',
            value: featuredCategories,
            color: 'text-orange-600',
            bgColor: 'bg-orange-100',
          },
        ].map((stat, index) => (
          <motion.div
            key={stat.label}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
          >
            <Card>
              <CardContent className="p-6">
                <div className={`w-12 h-12 rounded-lg ${stat.bgColor} flex items-center justify-center mb-4`}>
                  <FolderTree className={`w-6 h-6 ${stat.color}`} />
                </div>
                <h3 className="text-sm text-[#6f6e72] mb-1">{stat.label}</h3>
                <p className="text-2xl font-bold text-[#1d1d1f]">{stat.value}</p>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>

      {/* Categories Tree */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-bold text-[#1d1d1f]">
              Cây Danh Mục
            </h2>
            <Button
              variant="outline"
              size="sm"
              onClick={() => {
                if (expandedCategories.size === 0) {
                  const allIds = new Set(categories.map(c => c.id));
                  setExpandedCategories(allIds);
                } else {
                  setExpandedCategories(new Set());
                }
              }}
            >
              {expandedCategories.size === 0 ? 'Mở Tất Cả' : 'Thu Gọn'}
            </Button>
          </div>
        </CardHeader>
        <CardContent className="p-0">
          <div className="divide-y divide-gray-200">
            {mainCategories.map(category => renderCategory(category, 0))}
          </div>

          {mainCategories.length === 0 && (
            <div className="text-center py-12">
              <FolderTree className="w-16 h-16 mx-auto mb-4 text-[#6f6e72]" />
              <p className="text-[#6f6e72] text-lg">Chưa có danh mục nào</p>
              <Button className="mt-4">
                <Plus className="w-5 h-5 mr-2" />
                Tạo Danh Mục Đầu Tiên
              </Button>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Tips */}
      <Card className="mt-6">
        <CardContent className="p-6">
          <h3 className="font-bold text-[#1d1d1f] mb-4">💡 Mẹo Quản Lý Danh Mục</h3>
          <ul className="space-y-2 text-sm text-[#6f6e72]">
            <li>• Sử dụng cấu trúc phân cấp để tổ chức sản phẩm dễ dàng</li>
            <li>• Đặt danh mục nổi bật để hiển thị trên trang chủ</li>
            <li>• Thêm mô tả chi tiết để khách hàng dễ tìm kiếm</li>
            <li>• Sử dụng icon/emoji để danh mục dễ nhận diện hơn</li>
          </ul>
        </CardContent>
      </Card>
    </div>
  );
}
