import { useEffect, useState } from 'react';
import { useHistory } from 'react-router';
import { Link } from 'react-router-dom';
import { makeRequest } from 'core/utils/request';
import { ProductsResponse } from 'core/types/Product';
import Card from '../Card';
import Pagination from 'core/Pagination';

const List = () => {

    const [productsResponse, setProductsResponse] = useState<ProductsResponse>();

    const [isLoading, setIsLoading] = useState(false);

    const [activePage, setActivePage] = useState(0);

    const history = useHistory();

    useEffect(() => {

     const params = {
        page: activePage,
        linesPerPage: 4,
        direction: 'DESC',
        orderBy: 'id'
     }

     setIsLoading(true);
     makeRequest({ url: '/products', params})         
       .then(response => setProductsResponse(response.data))
       .finally(() => {
          setIsLoading(false);
       });

    }, [activePage]);    

    const handleCreate = () => {
        history.push('/admin/products/create');
    }

    return (
        <div className="admin-products-list">
            <button className="btn btn-primary btn-lg" onClick={handleCreate}>
                 ADICIONAR
            </button>
            <div className="admin-list-container">
                {productsResponse?.content.map(product => (
                    <Card key={product.id} product={product} />
                ))}

                {productsResponse && (
                    <Pagination
                       totalPage={productsResponse.totalPages}
                       activePage={activePage}
                       onChange={page => setActivePage(page)}
                    />
                )}
            </div>
        </div>
    );
}

export default List;