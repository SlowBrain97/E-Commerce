'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { 
  Package, 
  Clock, 
  CheckCircle, 
  XCircle,
  Truck,
  Search,
  Eye,
  Download
} from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Card, CardContent } from '@/components/ui/Card';
import { Pagination } from '@/components/common/Pagination';
import { formatPrice, formatDate } from '@/lib/utils/format';
import Link from 'next/link';

interface Order {
  orderId: number;
  customerName: string;
  customerEmail: string;
  totalAmount: number;
  status: string;
  orderDate: string;
  itemCount: number;
  paymentMethod: string;
  shippingAddress: string;
}

export default function DashboardOrdersPage() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    // Mock data - sẵn sàng tích hợp API
    const mockOrders: Order[] = [
      {
        orderId: 1001,
        customerName: 'Nguyễn Văn A',
        customerEmail: 'nguyenvana@email.com',
        totalAmount: 2500000,
        status: 'PENDING',
        orderDate: '2024-01-15T10:30:00',
        itemCount: 2,
        paymentMethod: 'COD',
        shippingAddress: '123 Đường ABC, Quận 1, TP.HCM',
      },
      {
        orderId: 1002,
        customerName: 'Trần Thị B',
        customerEmail: 'tranthib@email.com',
        totalAmount: 1800000,
        status: 'PROCESSING',
        orderDate: '2024-01-14T14:20:00',
        itemCount: 1,
        paymentMethod: 'CREDIT_CARD',
        shippingAddress: '456 Đường XYZ, Quận 2, TP.HCM',
      },
      {
        orderId: 1003,
        customerName: 'Lê Văn C',
        customerEmail: 'levanc@email.com',
        totalAmount: 3200000,
        status: 'SHIPPED',
        orderDate: '2024-01-13T09:15:00',
        itemCount: 3,
        paymentMethod: 'BANK_TRANSFER',
        shippingAddress: '789 Đường DEF, Quận 3, TP.HCM',
      },
      {
        orderId: 1004,
        customerName: 'Phạm Thị D',
        customerEmail: 'phamthid@email.com',
        totalAmount: 1500000,
        status: 'DELIVERED',
        orderDate: '2024-01-12T16:45:00',
        itemCount: 1,
        paymentMethod: 'COD',
        shippingAddress: '321 Đường GHI, Quận 4, TP.HCM',
      },
      {
        orderId: 1005,
        customerName: 'Hoàng Văn E',
        customerEmail: 'hoangvane@email.com',
        totalAmount: 2100000,
        status: 'CANCELLED',
        orderDate: '2024-01-11T11:30:00',
        itemCount: 2,
        paymentMethod: 'CREDIT_CARD',
        shippingAddress: '654 Đường JKL, Quận 5, TP.HCM',
      },
    ];

    setOrders(mockOrders);
    setTotalPages(1);
    setIsLoading(false);
  }, [page, searchQuery, statusFilter]);

  const getStatusBadge = (status: string) => {
    const statusConfig: Record<string, { variant: 'default' | 'info' | 'success' | 'warning' | 'error', label: string }> = {
      PENDING: { variant: 'warning', label: 'Chờ Xử Lý' },
      PROCESSING: { variant: 'info', label: 'Đang Xử Lý' },
      SHIPPED: { variant: 'info', label: 'Đang Giao' },
      DELIVERED: { variant: 'success', label: 'Đã Giao' },
      CANCELLED: { variant: 'error', label: 'Đã Hủy' },
    };

    const config = statusConfig[status] || { variant: 'default' as const, label: status };
    return <Badge variant={config.variant}>{config.label}</Badge>;
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'PENDING':
        return <Clock className="w-5 h-5 text-orange-600" />;
      case 'PROCESSING':
        return <Package className="w-5 h-5 text-blue-600" />;
      case 'SHIPPED':
        return <Truck className="w-5 h-5 text-blue-600" />;
      case 'DELIVERED':
        return <CheckCircle className="w-5 h-5 text-green-600" />;
      case 'CANCELLED':
        return <XCircle className="w-5 h-5 text-red-600" />;
      default:
        return <Package className="w-5 h-5 text-gray-600" />;
    }
  };

  if (isLoading) {
    return (
      <div className="container-custom py-12">
        <div className="animate-pulse space-y-4">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="card p-6">
              <div className="h-4 bg-gray-200 rounded w-1/3 mb-2" />
              <div className="h-4 bg-gray-200 rounded w-1/4" />
            </div>
          ))}
        </div>
      </div>
    );
  }

  const orderStats = {
    total: orders.length,
    pending: orders.filter(o => o.status === 'PENDING').length,
    processing: orders.filter(o => o.status === 'PROCESSING').length,
    shipped: orders.filter(o => o.status === 'SHIPPED').length,
    delivered: orders.filter(o => o.status === 'DELIVERED').length,
    cancelled: orders.filter(o => o.status === 'CANCELLED').length,
  };

  return (
    <div className="container-custom py-12">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold text-[#1d1d1f] mb-2">
            Quản Lý Đơn Hàng
          </h1>
          <p className="text-[#6f6e72]">
            Theo dõi và xử lý đơn hàng
          </p>
        </div>
        <Button variant="outline" size="lg">
          <Download className="w-5 h-5 mr-2" />
          Xuất Excel
        </Button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 mb-8">
        {[
          { label: 'Tổng Đơn', value: orderStats.total, color: 'text-gray-600', bgColor: 'bg-gray-100' },
          { label: 'Chờ Xử Lý', value: orderStats.pending, color: 'text-orange-600', bgColor: 'bg-orange-100' },
          { label: 'Đang Xử Lý', value: orderStats.processing, color: 'text-blue-600', bgColor: 'bg-blue-100' },
          { label: 'Đang Giao', value: orderStats.shipped, color: 'text-blue-600', bgColor: 'bg-blue-100' },
          { label: 'Đã Giao', value: orderStats.delivered, color: 'text-green-600', bgColor: 'bg-green-100' },
          { label: 'Đã Hủy', value: orderStats.cancelled, color: 'text-red-600', bgColor: 'bg-red-100' },
        ].map((stat, index) => (
          <motion.div
            key={stat.label}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.05 }}
          >
            <Card>
              <CardContent className="p-4">
                <p className="text-xs text-[#6f6e72] mb-1">{stat.label}</p>
                <p className={`text-2xl font-bold ${stat.color}`}>{stat.value}</p>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>

      {/* Search and Filters */}
      <Card className="mb-6">
        <CardContent className="p-6">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-[#6f6e72]" />
                <input
                  type="text"
                  placeholder="Tìm kiếm đơn hàng (ID, tên khách hàng, email)..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-[#1d1d1f]"
                />
              </div>
            </div>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-[#1d1d1f]"
            >
              <option value="ALL">Tất cả trạng thái</option>
              <option value="PENDING">Chờ xử lý</option>
              <option value="PROCESSING">Đang xử lý</option>
              <option value="SHIPPED">Đang giao</option>
              <option value="DELIVERED">Đã giao</option>
              <option value="CANCELLED">Đã hủy</option>
            </select>
          </div>
        </CardContent>
      </Card>

      {/* Orders List */}
      <div className="space-y-4">
        {orders.map((order, index) => (
          <motion.div
            key={order.orderId}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.05 }}
          >
            <Card>
              <CardContent className="p-6">
                <div className="flex flex-col lg:flex-row lg:items-center gap-6">
                  {/* Order Info */}
                  <div className="flex-1">
                    <div className="flex items-start gap-4 mb-4">
                      <div className="p-3 rounded-lg bg-gray-100">
                        {getStatusIcon(order.status)}
                      </div>
                      <div className="flex-1">
                        <div className="flex items-center gap-3 mb-2">
                          <h3 className="text-lg font-bold text-[#1d1d1f]">
                            Đơn Hàng #{order.orderId}
                          </h3>
                          {getStatusBadge(order.status)}
                        </div>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-2 text-sm">
                          <div>
                            <span className="text-[#6f6e72]">Khách hàng: </span>
                            <span className="font-semibold text-[#1d1d1f]">{order.customerName}</span>
                          </div>
                          <div>
                            <span className="text-[#6f6e72]">Email: </span>
                            <span className="text-[#1d1d1f]">{order.customerEmail}</span>
                          </div>
                          <div>
                            <span className="text-[#6f6e72]">Ngày đặt: </span>
                            <span className="text-[#1d1d1f]">{formatDate(order.orderDate)}</span>
                          </div>
                          <div>
                            <span className="text-[#6f6e72]">Số sản phẩm: </span>
                            <span className="text-[#1d1d1f]">{order.itemCount} sản phẩm</span>
                          </div>
                          <div>
                            <span className="text-[#6f6e72]">Thanh toán: </span>
                            <span className="text-[#1d1d1f]">
                              {order.paymentMethod === 'COD' ? 'COD' : 
                               order.paymentMethod === 'CREDIT_CARD' ? 'Thẻ tín dụng' : 
                               'Chuyển khoản'}
                            </span>
                          </div>
                          <div className="md:col-span-2">
                            <span className="text-[#6f6e72]">Địa chỉ: </span>
                            <span className="text-[#1d1d1f]">{order.shippingAddress}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Order Total and Actions */}
                  <div className="flex flex-col items-end gap-4 lg:min-w-[200px]">
                    <div className="text-right">
                      <p className="text-sm text-[#6f6e72] mb-1">Tổng tiền</p>
                      <p className="text-2xl font-bold text-[#1d1d1f]">
                        {formatPrice(order.totalAmount)}
                      </p>
                    </div>
                    <div className="flex gap-2">
                      <Button variant="outline" size="sm">
                        <Eye className="w-4 h-4 mr-2" />
                        Chi Tiết
                      </Button>
                      {order.status === 'PENDING' && (
                        <Button size="sm">
                          Xử Lý
                        </Button>
                      )}
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>

      {orders.length === 0 && (
        <Card>
          <CardContent className="p-12 text-center">
            <Package className="w-16 h-16 mx-auto mb-4 text-[#6f6e72]" />
            <p className="text-[#6f6e72] text-lg">Chưa có đơn hàng nào</p>
          </CardContent>
        </Card>
      )}

      {/* Pagination */}
      <Pagination
        currentPage={page}
        totalPages={totalPages}
        onPageChange={setPage}
      />
    </div>
  );
}
