import { useEffect, useState, useCallback } from 'react';
import { useHistory } from 'react-router';
import { toast } from 'react-toastify';

import { makePrivateRequest, makeRequest } from 'core/utils/request';
import { ProductsResponse } from 'core/types/Product';
import Card from '../Card';
import CardLoader from '../Loaders/ProductCardLoader';
import Pagination from 'core/Pagination';

const List = () => {

    const [productsResponse, setProductsResponse] = useState<ProductsResponse>();

    const [isLoading, setIsLoading] = useState(false);

    const [activePage, setActivePage] = useState(0);

    const history = useHistory();

    const getProducts = useCallback(() => {

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

    useEffect(() => {
        getProducts();
    }, [getProducts]);

    const handleCreate = () => {
        history.push('/admin/products/create');
    }

    const onRemove = (produtId: number) => {

        const confirm = window.confirm('Deseja relamente excluir este produto?');

        if (confirm) {

            makePrivateRequest({ url: `/products/${produtId}`, method: 'DELETE'})
            .then(() => {
                toast.info('Produto removido com sucesso!');
                getProducts();
            }).catch(() => {
                toast.error('Erro ao remover produto!');
            });
        }
    }

    return (
        <div className="admin-products-list">
            <button className="btn btn-primary btn-lg" onClick={handleCreate}>
                 ADICIONAR
            </button>
            <div className="admin-list-container">
                {isLoading ? <CardLoader /> : (
                    productsResponse?.content.map(product => (
                        <Card key={product.id} product={product} onRemove={onRemove}/>
                    ))
                )}
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