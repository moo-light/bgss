'use client'

import useSWR from 'swr';
import { useMemo } from 'react';
import { fetcher, endpoints } from '../../../src/utils/axios';

export function useSearchProducts(search) {
  const URL = search ? `${endpoints.productsearch}?typeGoldName=24k gold&search=${search}` : '';

  const { data, isLoading, error, isValidating } = useSWR(URL, fetcher, {
    keepPreviousData: true,
  });

  const memoizedValue = useMemo(() => {
    const searchResults =
      data?.data.map((product) => ({
        id: product?.id,
        name: product?.productName,
        coverUrl: product?.productImages?.[0]?.imgUrl || null,
        unitOfStock: product?.unitOfStock,
        typeGold: product?.typeGold?.typeName,
        unit: product?.typeGold?.goldUnit,
        weight: product?.weight,
        rating: product?.avgReview,
        category: product?.category?.categoryName,
      })) || [];

    return {
      searchResults,
      searchLoading: isLoading,
      searchError: error,
      searchValidating: isValidating,
      searchEmpty: !isLoading && searchResults.length === 0,
    };
  }, [data, error, isLoading, isValidating]);

  return memoizedValue;
}
