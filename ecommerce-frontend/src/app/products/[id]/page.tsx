'use client';

import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import Image from 'next/image';
import { motion } from 'framer-motion';
import { ShoppingCart, Heart, Star, Truck, Shield, RotateCcw, Minus, Plus } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { ProductGrid } from '@/components/products/ProductGrid';
import { productsApi } from '@/lib/api/products';
import { reviewsApi } from '@/lib/api/reviews';
import { useCartStore } from '@/lib/store/cartStore';
import { ProductResponse, ReviewResponse } from '@/lib/types/api';
import { formatPrice, formatRelativeTime } from '@/lib/utils/format';
import { toast } from 'react-hot-toast';

export default function ProductDetailPage() {
  const params = useParams();
  const productId = Number(params.id);
  
  const [product, setProduct] = useState<ProductResponse | null>(null);
  const [relatedProducts, setRelatedProducts] = useState<ProductResponse[]>([]);
  const [reviews, setReviews] = useState<ReviewResponse[]>([]);
  const [selectedImage, setSelectedImage] = useState(0);
  const [selectedVariant, setSelectedVariant] = useState<number | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [isLoading, setIsLoading] = useState(true);
  
  const { addToCart } = useCartStore();

  useEffect(() => {
    if (productId) {
      fetchProductData();
    }
  }, [productId]);

  const fetchProductData = async () => {
    try {
      setIsLoading(true);
      
      const [productRes, relatedRes, reviewsRes] = await Promise.all([
        productsApi.getProductById(productId),
        productsApi.getRelatedProducts(productId),
        reviewsApi.getProductReviews(productId, { page: 0, size: 5 }),
      ]);

      if (productRes.success) {
        setProduct(productRes.data);
      }

      if (relatedRes.success) {
        setRelatedProducts(relatedRes.data);
      }

      if (reviewsRes.success) {
        setReviews(reviewsRes.data.data);
      }
    } catch (error) {
      console.error('Failed to fetch product data:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleAddToCart = async () => {
    if (!product) return;

    try {
      await addToCart({
        productId: product.id.toString(),
        variantId: selectedVariant?.toString(),
        quantity,
      });
    } catch (error) {
      console.error('Failed to add to cart:', error);
    }
  };

  if (isLoading) {
    return (
      <div className="container-custom py-12">
        <div className="animate-pulse">
          <div className="grid md:grid-cols-2 gap-12">
            <div className="aspect-square bg-gray-200 rounded-xl" />
            <div className="space-y-4">
              <div className="h-8 bg-gray-200 rounded w-3/4" />
              <div className="h-6 bg-gray-200 rounded w-1/2" />
              <div className="h-24 bg-gray-200 rounded" />
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="container-custom py-12 text-center">
        <h1 className="text-2xl font-bold text-main-text">Product not found</h1>
      </div>
    );
  }

  const currentPrice = selectedVariant
    ? product.variants?.find(v => v.id === selectedVariant)?.price || product.price
    : product.price;

  const discount = product.compareAtPrice
    ? Math.round(((product.compareAtPrice - currentPrice) / product.compareAtPrice) * 100)
    : 0;

  return (
    <div>
      {/* Product Details */}
      <div className="container-custom py-12">
        <div className="grid md:grid-cols-2 gap-12">
          {/* Images */}
          <div>
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="aspect-square rounded-xl overflow-hidden bg-gray-100 mb-4"
            >
              <Image
                src={product.allImageUrls[selectedImage] || product.primaryImageUrl || '/placeholder-shoe.jpg'}
                alt={product.name}
                width={600}
                height={600}
                className="w-full h-full object-cover"
              />
            </motion.div>

            {/* Thumbnail Gallery */}
            {product.allImageUrls.length > 1 && (
              <div className="grid grid-cols-4 gap-4">
                {product.allImageUrls.map((url, index) => (
                  <button
                    key={index}
                    onClick={() => setSelectedImage(index)}
                    className={`aspect-square rounded-lg overflow-hidden border-2 transition-all ${
                      selectedImage === index
                        ? 'border-main-text'
                        : 'border-transparent hover:border-gray-300'
                    }`}
                  >
                    <Image
                      src={url}
                      alt={`${product.name} ${index + 1}`}
                      width={150}
                      height={150}
                      className="w-full h-full object-cover"
                    />
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Product Info */}
          <div>
            <div className="mb-4">
              <Badge variant="default">{product.category.name}</Badge>
              {product.isFeatured && (
                <Badge variant="info" className="ml-2">Featured</Badge>
              )}
            </div>

            <h1 className="text-3xl md:text-4xl font-bold text-main-text mb-4">
              {product.name}
            </h1>

            {/* Rating */}
            {product.reviewCount > 0 && (
              <div className="flex items-center gap-2 mb-4">
                <div className="flex">
                  {[...Array(5)].map((_, i) => (
                    <Star
                      key={i}
                      className={`w-5 h-5 ${
                        i < Math.round(product.averageRating)
                          ? 'text-yellow-400 fill-yellow-400'
                          : 'text-gray-300'
                      }`}
                    />
                  ))}
                </div>
                <span className="text-semi-text">
                  {product.averageRating.toFixed(1)} ({product.reviewCount} reviews)
                </span>
              </div>
            )}

            {/* Price */}
            <div className="flex items-center gap-4 mb-6">
              <span className="text-3xl font-bold text-main-text">
                {formatPrice(currentPrice)}
              </span>
              {product.compareAtPrice && (
                <>
                  <span className="text-xl text-semi-text line-through">
                    {formatPrice(product.compareAtPrice)}
                  </span>
                  <Badge variant="error">-{discount}%</Badge>
                </>
              )}
            </div>

            {/* Description */}
            <p className="text-semi-text mb-6 leading-relaxed">
              {product.description || product.shortDescription}
            </p>

            {/* Variants */}
            {product.variants && product.variants.length > 0 && (
              <div className="mb-6">
                <h3 className="font-semibold text-main-text mb-3">Select Size</h3>
                <div className="flex flex-wrap gap-2">
                  {product.variants.map((variant) => (
                    <button
                      key={variant.id}
                      onClick={() => setSelectedVariant(variant.id!)}
                      disabled={variant.stockQuantity === 0}
                      className={`px-4 py-2 rounded-lg border-2 font-medium transition-all ${
                        selectedVariant === variant.id
                          ? 'border-main-text bg-main-text text-white'
                          : variant.stockQuantity === 0
                          ? 'border-gray-200 bg-gray-100 text-gray-400 cursor-not-allowed'
                          : 'border-gray-300 hover:border-main-text'
                      }`}
                    >
                      {variant.variantValue}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Quantity */}
            <div className="mb-6">
              <h3 className="font-semibold text-main-text mb-3">Quantity</h3>
              <div className="flex items-center gap-4">
                <div className="flex items-center border-2 border-gray-300 rounded-lg">
                  <button
                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                    className="p-3 hover:bg-gray-100 transition-colors"
                  >
                    <Minus className="w-4 h-4" />
                  </button>
                  <span className="px-6 font-semibold">{quantity}</span>
                  <button
                    onClick={() => setQuantity(quantity + 1)}
                    className="p-3 hover:bg-gray-100 transition-colors"
                  >
                    <Plus className="w-4 h-4" />
                  </button>
                </div>
                {product.stockQuantity > 0 && product.stockQuantity < 10 && (
                  <span className="text-orange-600 text-sm">
                    Only {product.stockQuantity} left in stock
                  </span>
                )}
              </div>
            </div>

            {/* Actions */}
            <div className="flex gap-4 mb-8">
              <Button
                size="lg"
                className="flex-1"
                onClick={handleAddToCart}
                disabled={product.stockQuantity === 0}
              >
                <ShoppingCart className="w-5 h-5 mr-2" />
                {product.stockQuantity === 0 ? 'Out of Stock' : 'Add to Cart'}
              </Button>
              <Button variant="outline" size="lg">
                <Heart className="w-5 h-5" />
              </Button>
            </div>

            {/* Features */}
            <div className="grid grid-cols-3 gap-4 pt-8 border-t border-gray-200">
              <div className="text-center">
                <Truck className="w-8 h-8 mx-auto mb-2 text-main-text" />
                <p className="text-sm text-semi-text">Free Shipping</p>
              </div>
              <div className="text-center">
                <Shield className="w-8 h-8 mx-auto mb-2 text-main-text" />
                <p className="text-sm text-semi-text">Secure Payment</p>
              </div>
              <div className="text-center">
                <RotateCcw className="w-8 h-8 mx-auto mb-2 text-main-text" />
                <p className="text-sm text-semi-text">Easy Returns</p>
              </div>
            </div>
          </div>
        </div>

        {/* Reviews */}
        {reviews.length > 0 && (
          <div className="mt-16">
            <h2 className="text-2xl font-bold text-main-text mb-6">Customer Reviews</h2>
            <div className="space-y-6">
              {reviews.map((review) => (
                <div key={review.id} className="card p-6">
                  <div className="flex items-start justify-between mb-4">
                    <div>
                      <div className="flex items-center gap-2 mb-2">
                        <span className="font-semibold text-main-text">{review.userName}</span>
                        {review.isVerifiedPurchase && (
                          <Badge variant="success" className="text-xs">Verified Purchase</Badge>
                        )}
                      </div>
                      <div className="flex items-center gap-2">
                        <div className="flex">
                          {[...Array(5)].map((_, i) => (
                            <Star
                              key={i}
                              className={`w-4 h-4 ${
                                i < review.rating
                                  ? 'text-yellow-400 fill-yellow-400'
                                  : 'text-gray-300'
                              }`}
                            />
                          ))}
                        </div>
                        <span className="text-sm text-semi-text">
                          {formatRelativeTime(review.createdAt)}
                        </span>
                      </div>
                    </div>
                  </div>
                  {review.title && (
                    <h4 className="font-semibold text-main-text mb-2">{review.title}</h4>
                  )}
                  <p className="text-semi-text">{review.comment}</p>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Related Products */}
      {relatedProducts.length > 0 && (
        <div className="bg-white py-16">
          <div className="container-custom">
            <h2 className="text-2xl font-bold text-main-text mb-8">You May Also Like</h2>
            <ProductGrid products={relatedProducts.slice(0, 4)} />
          </div>
        </div>
      )}
    </div>
  );
}
