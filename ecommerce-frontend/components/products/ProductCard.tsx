'use client';

import React from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { motion } from 'framer-motion';
import { ShoppingCart, Heart, Star } from 'lucide-react';
import { ProductResponse } from '@/lib/types/api';
import { formatPrice } from '@/lib/utils/format';
import { useCartStore } from '@/lib/store/cartStore';
import { toast } from 'react-hot-toast';

interface ProductCardProps {
  product: ProductResponse;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product }) => {
  const { addToCart } = useCartStore();
  const [isWishlisted, setIsWishlisted] = React.useState(false);

  const handleAddToCart = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    
    try {
      await addToCart({
        productId: product.id.toString(),
        quantity: 1,
      });
    } catch (error) {
      console.error('Failed to add to cart:', error);
    }
  };

  const handleWishlist = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setIsWishlisted(!isWishlisted);
    toast.success(isWishlisted ? 'Removed from wishlist' : 'Added to wishlist');
  };

  const discount = product.compareAtPrice
    ? Math.round(((product.compareAtPrice - product.price) / product.compareAtPrice) * 100)
    : 0;

  return (
    <Link href={`/products/${product.id}`}>
      <motion.div
        whileHover={{ y: -8 }}
        className="card overflow-hidden group cursor-pointer h-full"
      >
        {/* Image Container */}
        <div className="relative aspect-square overflow-hidden bg-gray-100">
          <Image
            src={product.primaryImageUrl || '/placeholder-shoe.jpg'}
            alt={product.name}
            fill
            sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
            className="object-cover group-hover:scale-110 transition-transform duration-500"
          />
          
          {/* Badges */}
          <div className="absolute top-3 left-3 flex flex-col gap-2">
            {product.isFeatured && (
              <span className="bg-main-text text-white text-xs font-bold px-2 py-1 rounded">
                FEATURED
              </span>
            )}
            {discount > 0 && (
              <span className="bg-red-500 text-white text-xs font-bold px-2 py-1 rounded">
                -{discount}%
              </span>
            )}
            {product.stockQuantity === 0 && (
              <span className="bg-gray-800 text-white text-xs font-bold px-2 py-1 rounded">
                SOLD OUT
              </span>
            )}
          </div>

          {/* Actions */}
          <div className="absolute top-3 right-3 flex flex-col gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
            <motion.button
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
              onClick={handleWishlist}
              className={`p-2 rounded-full bg-white shadow-md ${
                isWishlisted ? 'text-red-500' : 'text-semi-text'
              } hover:text-red-500 transition-colors`}
            >
              <Heart className="w-5 h-5" fill={isWishlisted ? 'currentColor' : 'none'} />
            </motion.button>
            
            {product.stockQuantity > 0 && (
              <motion.button
                whileHover={{ scale: 1.1 }}
                whileTap={{ scale: 0.9 }}
                onClick={handleAddToCart}
                className="p-2 rounded-full bg-main-text text-white shadow-md hover:bg-opacity-90 transition-colors"
              >
                <ShoppingCart className="w-5 h-5" />
              </motion.button>
            )}
          </div>
        </div>

        {/* Content */}
        <div className="p-4">
          {/* Category */}
          <p className="text-xs text-semi-text uppercase tracking-wide mb-1">
            {product.category.name}
          </p>

          {/* Name */}
          <h3 className="font-semibold text-main-text mb-2 line-clamp-2 group-hover:text-semi-text transition-colors">
            {product.name}
          </h3>

          {/* Rating */}
          {product.reviewCount > 0 && (
            <div className="flex items-center gap-1 mb-2">
              <div className="flex">
                {[...Array(5)].map((_, i) => (
                  <Star
                    key={i}
                    className={`w-4 h-4 ${
                      i < Math.round(product.averageRating)
                        ? 'text-yellow-400 fill-yellow-400'
                        : 'text-gray-300'
                    }`}
                  />
                ))}
              </div>
              <span className="text-xs text-semi-text">
                ({product.reviewCount})
              </span>
            </div>
          )}

          {/* Price */}
          <div className="flex items-center gap-2">
            <span className="text-lg font-bold text-main-text">
              {formatPrice(product.price)}
            </span>
            {product.compareAtPrice && (
              <span className="text-sm text-semi-text line-through">
                {formatPrice(product.compareAtPrice)}
              </span>
            )}
          </div>

          {/* Stock Status */}
          {product.stockQuantity > 0 && product.stockQuantity < 10 && (
            <p className="text-xs text-orange-600 mt-2">
              Only {product.stockQuantity} left in stock
            </p>
          )}
        </div>
      </motion.div>
    </Link>
  );
};
