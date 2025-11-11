'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { 
  Star, 
  MessageSquare, 
  CheckCircle, 
  XCircle,
  Search,
  Eye,
  Trash2
} from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Card, CardContent } from '@/components/ui/Card';
import { Pagination } from '@/components/common/Pagination';
import { formatDate } from '@/lib/utils/format';
import { toast } from 'react-hot-toast';
import Link from 'next/link';

interface Review {
  id: number;
  productId: number;
  productName: string;
  userId: number;
  userName: string;
  rating: number;
  title: string;
  comment: string;
  isVerifiedPurchase: boolean;
  status: string;
  createdAt: string;
}

export default function DashboardReviewsPage() {
  const [reviews, setReviews] = useState<Review[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [ratingFilter, setRatingFilter] = useState<number>(0);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    // Mock data - sẵn sàng tích hợp API
    const mockReviews: Review[] = [
      {
        id: 1,
        productId: 101,
        productName: 'Nike Air Max 2024',
        userId: 1,
        userName: 'Nguyễn Văn A',
        rating: 5,
        title: 'Sản phẩm tuyệt vời!',
        comment: 'Giày rất đẹp và chất lượng, đi rất êm chân. Giao hàng nhanh, đóng gói cẩn thận.',
        isVerifiedPurchase: true,
        status: 'APPROVED',
        createdAt: '2024-01-15T10:30:00',
      },
      {
        id: 2,
        productId: 102,
        productName: 'Adidas Ultraboost',
        userId: 2,
        userName: 'Trần Thị B',
        rating: 4,
        title: 'Tốt nhưng hơi đắt',
        comment: 'Chất lượng tốt, thiết kế đẹp. Tuy nhiên giá hơi cao so với mặt bằng chung.',
        isVerifiedPurchase: true,
        status: 'PENDING',
        createdAt: '2024-01-14T14:20:00',
      },
      {
        id: 3,
        productId: 103,
        productName: 'Puma RS-X',
        userId: 3,
        userName: 'Lê Văn C',
        rating: 3,
        title: 'Bình thường',
        comment: 'Sản phẩm ổn, không có gì đặc biệt.',
        isVerifiedPurchase: false,
        status: 'APPROVED',
        createdAt: '2024-01-13T09:15:00',
      },
      {
        id: 4,
        productId: 104,
        productName: 'New Balance 574',
        userId: 4,
        userName: 'Phạm Thị D',
        rating: 2,
        title: 'Không như mong đợi',
        comment: 'Size không chuẩn, chất liệu không tốt như mô tả.',
        isVerifiedPurchase: true,
        status: 'REJECTED',
        createdAt: '2024-01-12T16:45:00',
      },
    ];

    setReviews(mockReviews);
    setTotalPages(1);
    setIsLoading(false);
  }, [page, searchQuery, statusFilter, ratingFilter]);

  const handleApprove = async (reviewId: number) => {
    try {
      toast.success('Đã duyệt đánh giá');
      // Refresh reviews
    } catch (error) {
      console.error('Failed to approve review:', error);
      toast.error('Không thể duyệt đánh giá');
    }
  };

  const handleReject = async (reviewId: number) => {
    try {
      toast.success('Đã từ chối đánh giá');
      // Refresh reviews
    } catch (error) {
      console.error('Failed to reject review:', error);
      toast.error('Không thể từ chối đánh giá');
    }
  };

  const handleDelete = async (reviewId: number) => {
    if (!confirm('Bạn có chắc muốn xóa đánh giá này?')) return;

    try {
      toast.success('Đã xóa đánh giá');
      // Refresh reviews
    } catch (error) {
      console.error('Failed to delete review:', error);
      toast.error('Không thể xóa đánh giá');
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'APPROVED':
        return <Badge variant="success">Đã Duyệt</Badge>;
      case 'PENDING':
        return <Badge variant="warning">Chờ Duyệt</Badge>;
      case 'REJECTED':
        return <Badge variant="error">Đã Từ Chối</Badge>;
      default:
        return <Badge variant="default">{status}</Badge>;
    }
  };

  if (isLoading) {
    return (
      <div className="container-custom py-12">
        <div className="animate-pulse space-y-4">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="card p-6">
              <div className="space-y-2">
                <div className="h-4 bg-gray-200 rounded w-1/3" />
                <div className="h-4 bg-gray-200 rounded w-full" />
                <div className="h-4 bg-gray-200 rounded w-2/3" />
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  const reviewStats = {
    total: reviews.length,
    pending: reviews.filter(r => r.status === 'PENDING').length,
    approved: reviews.filter(r => r.status === 'APPROVED').length,
    rejected: reviews.filter(r => r.status === 'REJECTED').length,
    verified: reviews.filter(r => r.isVerifiedPurchase).length,
    avgRating: reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length || 0,
  };

  return (
    <div className="container-custom py-12">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold text-[#1d1d1f] mb-2">
            Quản Lý Đánh Giá
          </h1>
          <p className="text-[#6f6e72]">
            Kiểm duyệt và quản lý đánh giá sản phẩm
          </p>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 mb-8">
        {[
          { label: 'Tổng Đánh Giá', value: reviewStats.total, color: 'text-gray-600', bgColor: 'bg-gray-100' },
          { label: 'Chờ Duyệt', value: reviewStats.pending, color: 'text-orange-600', bgColor: 'bg-orange-100' },
          { label: 'Đã Duyệt', value: reviewStats.approved, color: 'text-green-600', bgColor: 'bg-green-100' },
          { label: 'Đã Từ Chối', value: reviewStats.rejected, color: 'text-red-600', bgColor: 'bg-red-100' },
          { label: 'Đã Mua Hàng', value: reviewStats.verified, color: 'text-blue-600', bgColor: 'bg-blue-100' },
          { label: 'Đánh Giá TB', value: reviewStats.avgRating.toFixed(1), color: 'text-yellow-600', bgColor: 'bg-yellow-100' },
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
                  placeholder="Tìm kiếm đánh giá..."
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
              <option value="PENDING">Chờ duyệt</option>
              <option value="APPROVED">Đã duyệt</option>
              <option value="REJECTED">Đã từ chối</option>
            </select>
            <select
              value={ratingFilter}
              onChange={(e) => setRatingFilter(Number(e.target.value))}
              className="px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-[#1d1d1f]"
            >
              <option value={0}>Tất cả đánh giá</option>
              <option value={5}>5 sao</option>
              <option value={4}>4 sao</option>
              <option value={3}>3 sao</option>
              <option value={2}>2 sao</option>
              <option value={1}>1 sao</option>
            </select>
          </div>
        </CardContent>
      </Card>

      {/* Reviews List */}
      <div className="space-y-4">
        {reviews.map((review, index) => (
          <motion.div
            key={review.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.05 }}
          >
            <Card>
              <CardContent className="p-6">
                <div className="flex flex-col lg:flex-row gap-6">
                  {/* Review Content */}
                  <div className="flex-1">
                    {/* Header */}
                    <div className="flex items-start justify-between mb-4">
                      <div className="flex-1">
                        <div className="flex items-center gap-3 mb-2">
                          <div className="flex">
                            {[...Array(5)].map((_, i) => (
                              <Star
                                key={i}
                                className={`w-5 h-5 ${
                                  i < review.rating
                                    ? 'text-yellow-400 fill-yellow-400'
                                    : 'text-gray-300'
                                }`}
                              />
                            ))}
                          </div>
                          {getStatusBadge(review.status)}
                          {review.isVerifiedPurchase && (
                            <Badge variant="success" className="text-xs">
                              Đã mua hàng
                            </Badge>
                          )}
                        </div>
                        <h3 className="font-bold text-[#1d1d1f] mb-1">
                          {review.title}
                        </h3>
                        <div className="flex items-center gap-3 text-sm text-[#6f6e72]">
                          <span className="font-semibold">{review.userName}</span>
                          <span>•</span>
                          <span>{formatDate(review.createdAt)}</span>
                          <span>•</span>
                          <Link 
                            href={`/products/${review.productId}`}
                            className="text-[#1d1d1f] hover:underline"
                          >
                            {review.productName}
                          </Link>
                        </div>
                      </div>
                    </div>

                    {/* Review Text */}
                    <p className="text-[#6f6e72] mb-4 leading-relaxed">
                      {review.comment}
                    </p>
                  </div>

                  {/* Actions */}
                  <div className="flex flex-col gap-2 lg:min-w-[150px]">
                    {review.status === 'PENDING' && (
                      <>
                        <Button
                          size="sm"
                          onClick={() => handleApprove(review.id)}
                          className="w-full"
                        >
                          <CheckCircle className="w-4 h-4 mr-2" />
                          Duyệt
                        </Button>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleReject(review.id)}
                          className="w-full"
                        >
                          <XCircle className="w-4 h-4 mr-2" />
                          Từ Chối
                        </Button>
                      </>
                    )}
                    <Link href={`/products/${review.productId}`}>
                      <Button variant="outline" size="sm" className="w-full">
                        <Eye className="w-4 h-4 mr-2" />
                        Xem SP
                      </Button>
                    </Link>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleDelete(review.id)}
                      className="w-full text-red-600 hover:text-red-700"
                    >
                      <Trash2 className="w-4 h-4 mr-2" />
                      Xóa
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>

      {reviews.length === 0 && (
        <Card>
          <CardContent className="p-12 text-center">
            <MessageSquare className="w-16 h-16 mx-auto mb-4 text-[#6f6e72]" />
            <p className="text-[#6f6e72] text-lg">Chưa có đánh giá nào</p>
          </CardContent>
        </Card>
      )}

      {/* Pagination */}
      <Pagination
        currentPage={page}
        totalPages={totalPages}
        onPageChange={setPage}
      />

      {/* Tips */}
      <Card className="mt-6">
        <CardContent className="p-6">
          <h3 className="font-bold text-[#1d1d1f] mb-4">💡 Mẹo Kiểm Duyệt Đánh Giá</h3>
          <ul className="space-y-2 text-sm text-[#6f6e72]">
            <li>• Ưu tiên duyệt đánh giá từ người đã mua hàng</li>
            <li>• Từ chối đánh giá có nội dung spam hoặc không phù hợp</li>
            <li>• Theo dõi đánh giá tiêu cực để cải thiện sản phẩm</li>
            <li>• Trả lời đánh giá để tăng tương tác với khách hàng</li>
          </ul>
        </CardContent>
      </Card>
    </div>
  );
}
