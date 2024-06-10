import PropTypes from 'prop-types';
import Box from '@mui/material/Box';
import Pagination, { paginationClasses } from '@mui/material/Pagination';
import ProductItem from './product-item';
import { ProductItemSkeleton } from './product-skeleton';
import { useState } from 'react';

function ProductList({ products, loading, ...other }) {
  const [page, setPage] = useState(1);
  const itemsPerPage = 12; // số lượng sản phẩm trên mỗi trang

  const handleChangePage = (event, value) => {
    setPage(value);
  };

  const renderSkeleton = (
    <>
      {[...Array(itemsPerPage)].map((_, index) => (
        <ProductItemSkeleton key={index} />
      ))}
    </>
  );

  const renderList = (
    <>
      {products
        .slice((page - 1) * itemsPerPage, page * itemsPerPage)
        .map((product) => (
          <ProductItem key={product.id} product={product} />
        ))}
    </>
  );

  return (
    <>
      <Box
        gap={3}
        display="grid"
        gridTemplateColumns={{
          xs: 'repeat(1, 1fr)',
          sm: 'repeat(2, 1fr)',
          md: 'repeat(3, 1fr)',
          lg: 'repeat(4, 1fr)',
        }}
        {...other}
      >
        {loading ? renderSkeleton : renderList}
      </Box>
      {products.length > itemsPerPage && (
        <Pagination
          count={Math.ceil(products.length / itemsPerPage)}
          page={page}
          onChange={handleChangePage}
          sx={{
            mt: 8,
            backgroundColor: '#ffffff',
            [`& .${paginationClasses.ul}`]: {
              justifyContent: 'center',
            },
          }}
        />
      )}
    </>
  );
}

ProductList.propTypes = {
  loading: PropTypes.bool,
  products: PropTypes.array,
};

export default ProductList;
