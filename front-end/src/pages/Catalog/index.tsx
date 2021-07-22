import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { makeRequest } from 'core/utils/request';
import ProductCard from './components/ProductCard';
import { ProductsResponse } from 'core/types/Product';
import ProductCardLoader from './components/Loaders/ProductCarLoader';
import Pagination from 'core/Pagination';
import ProductFilters, { FilterForm } from 'core/components/ProductFilters';
import './styles.scss';

const Catalog = () => {

    const [productsResponse, setProductsResponse] = useState<ProductsResponse>();

    const [isLoading, setIsLoading] = useState(false);

    const [activePage, setActivePage] = useState(0);

    const getProduct = useCallback((filter?: FilterForm) => {

       const params = {
          page: activePage,
          linesPerPage: 10,
          name: filter?.name,
          categoryId: filter?.categoryId
       }
   
       setIsLoading(true);
       makeRequest({ url: '/products', params})
         .then(response => setProductsResponse(response.data))
         .finally(() => {
            setIsLoading(false);
         });
    },[activePage]);

    useEffect(() => {
      getProduct();
    }, [getProduct]);

   return (
     <div className="catalog-container">
        <div className="d-flex justify-content-between">
         <h1 className="catalog-title">
               Catálogo de produtos
         </h1>
         <ProductFilters onSearch={filter => getProduct(filter)}/>
        </div>
        <div className="catalog-products">
            { isLoading ? <ProductCardLoader /> : (
              productsResponse?.content.map(product => (
               <Link to={`/products/${product.id}`} key={product.id}>
                    <ProductCard product={product} />
               </Link>
             ))
            )}
        </div>
        {productsResponse && (
          <Pagination 
            totalPage={productsResponse.totalPages} 
            activePage={activePage} 
            onChange={page => setActivePage(page)}/>
         )}
     </div>    
   );
}

export default Catalog;
