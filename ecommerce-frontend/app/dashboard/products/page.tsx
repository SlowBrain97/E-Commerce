'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { 
  Plus, 
  Edit2, 
  Trash2, 
  Search, 
  Eye,
  Package,
  AlertCircle
} from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Card, CardContent } from '@/components/ui/Card';
import { Pagination } from '@/components/common/Pagination';
import { productsApi } from '@/lib/api/products';
import { ProductResponse } from '@/lib/types/api';
import { formatPrice } from '@/lib/utils/format';
import { toast } from 'react-hot-toast';
import Link from 'next/link';
import Image from 'next/image';

export default function DashboardProductsPage() {
  const [products, setProducts] = useState<ProductResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    fetchProducts();
  }, [page, searchQuery]);

  const fetchProducts = async () => {
    try {
      setIsLoading(true);
      const response = await productsApi.getAllProducts({
        page,
        size: 10,
        sortBy: 'id',
        sortDirection: 'desc',
      });

      if (response.success) {
        setProducts(response.data.data);
        setTotalPages(response.data.totalPages);
      }
    } catch (error) {
      console.error('Failed to fetch products:', error);
      toast.error('Không thể tải danh sách sản phẩm');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (productId: number) => {
    if (!confirm('Bạn có chắc muốn xóa sản phẩm này?')) return;

    try {
      await productsApi.deleteProduct(productId);
      toast.success('Đã xóa sản phẩm');
      fetchProducts();
    } catch (error) {
      console.error('Failed to delete product:', error);
      toast.error('Không thể xóa sản phẩm');
    }
  };

  if (isLoading) {
    return (
      <div className="container-custom py-12">
        <div className="animate-pulse space-y-4">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="card p-6">
              <div className="flex gap-4">
                <div className="w-20 h-20 bg-gray-200 rounded" />
                <div className="flex-1 space-y-2">
                  <div className="h-4 bg-gray-200 rounded w-1/3" />
                  <div className="h-4 bg-gray-200 rounded w-1/4" />
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  const stats = [
    {
      label: 'Tổng Sản Phẩm',
      value: products.length,
      color: 'text-blue-600',
      bgColor: 'bg-blue-100',
    },
    {
      label: 'Đang Hoạt Động',
      value: products.filter(p => p.isActive).length,
      color: 'text-green-600',
      bgColor: 'bg-green-100',
    },
    {
      label: 'Hết Hàng',
      value: products.filter(p => p.stockQuantity === 0).length,
      color: 'text-red-600',
      bgColor: 'bg-red-100',
    },
    {
      label: 'Tồn Kho Thấp',
      value: products.filter(p => p.stockQuantity > 0 && p.stockQuantity < 10).length,
      color: 'text-orange-600',
      bgColor: 'bg-orange-100',
    },
  ];

  return (
    <div className="container-custom py-12">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold text-[#1d1d1f] mb-2">
            Quản Lý Sản Phẩm
          </h1>
          <p className="text-[#6f6e72]">
            Quản lý tất cả sản phẩm trong cửa hàng
          </p>
        </div>
        <Button size="lg" className="group">
          <Plus className="w-5 h-5 mr-2" />
          Thêm Sản Phẩm
        </Button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        {stats.map((stat, index) => (
          <motion.div
            key={stat.label}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
          >
            <Card>
              <CardContent className="p-6">
                <div className={`w-12 h-12 rounded-lg ${stat.bgColor} flex items-center justify-center mb-4`}>
                  <Package className={`w-6 h-6 ${stat.color}`} />
                </div>
                <h3 className="text-sm text-[#6f6e72] mb-1">{stat.label}</h3>
                <p className="text-2xl font-bold text-[#1d1d1f]">{stat.value}</p>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>

      {/* Search */}
      <Card className="mb-6">
        <CardContent className="p-6">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-[#6f6e72]" />
            <input
              type="text"
              placeholder="Tìm kiếm sản phẩm..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-[#1d1d1f]"
            />
          </div>
        </CardContent>
      </Card>

      {/* Products Table */}
      <Card>
        <CardContent className="p-0">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-[#1d1d1f]">
                    Sản Phẩm
                  </th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-[#1d1d1f]">
                    Danh Mục
                  </th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-[#1d1d1f]">
                    Giá
                  </th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-[#1d1d1f]">
                    Tồn Kho
                  </th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-[#1d1d1f]">
                    Trạng Thái
                  </th>
                  <th className="px-6 py-4 text-right text-sm font-semibold text-[#1d1d1f]">
                    Thao Tác
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {products.map((product) => (
                  <tr key={product.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-4">
                        <div className="w-16 h-16 rounded-lg overflow-hidden bg-gray-100 flex-shrink-0">
                          <Image
                            src={product.primaryImageUrl || '/placeholder-shoe.jpg'}
                            alt={product.name}
                            width={64}
                            height={64}
                            className="w-full h-full object-cover"
                          />
                        </div>
                        <div>
                          <p className="font-semibold text-[#1d1d1f] line-clamp-1">
                            {product.name}
                          </p>
                          <p className="text-sm text-[#6f6e72] line-clamp-1">
                            {product.shortDescription}
                          </p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-sm text-[#6f6e72]">
                        {product.category.name}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div>
                        <p className="font-semibold text-[#1d1d1f]">
                          {formatPrice(product.price)}
                        </p>
                        {product.compareAtPrice && (
                          <p className="text-sm text-[#6f6e72] line-through">
                            {formatPrice(product.compareAtPrice)}
                          </p>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <span className={`font-semibold ${
                        product.stockQuantity === 0
                          ? 'text-red-600'
                          : product.stockQuantity < 10
                          ? 'text-orange-600'
                          : 'text-green-600'
                      }`}>
                        {product.stockQuantity}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <Badge variant={product.isActive ? 'success' : 'default'}>
                        {product.isActive ? 'Hoạt động' : 'Ẩn'}
                      </Badge>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center justify-end gap-2">
                        <Link href={`/products/${product.id}`}>
                          <Button variant="ghost" size="sm">
                            <Eye className="w-4 h-4" />
                          </Button>
                        </Link>
                        <Button variant="ghost" size="sm">
                          <Edit2 className="w-4 h-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleDelete(product.id)}
                          className="text-red-600 hover:text-red-700"
                        >
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {products.length === 0 && (
            <div className="text-center py-12">
              <Package className="w-16 h-16 mx-auto mb-4 text-[#6f6e72]" />
              <p className="text-[#6f6e72] text-lg">Chưa có sản phẩm nào</p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Pagination */}
      <Pagination
        currentPage={page}
        totalPages={totalPages}
        onPageChange={setPage}
      />
    </div>
  );
}
