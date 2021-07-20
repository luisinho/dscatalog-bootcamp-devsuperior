import { useEffect, useState } from 'react';
import { useHistory, useParams } from 'react-router';
import { useForm, Controller } from 'react-hook-form';
import { toast } from 'react-toastify';
import Select from 'react-select';

import { Category, FileProduct, getNewFileProduct, Product, getNewProduct } from 'core/types/Product';
import { makePrivateRequest, makeRequest } from 'core/utils/request';
import BaseForm from '../../BaseForm';
import PriceField from './PriceField';
import ImageUpload from '../ImageUpload';
import './styles.scss';

export type FormsState = {
    name: string;
    price: number;
    description: string;
    imgUrl: string;
    fileProduct: FileProduct;
    categories: Category[];
}

type ParamsType = {
    productId: string;
}

const Form = () => {

    const { register, handleSubmit, formState: { errors }, setValue, control } = useForm<FormsState>();

    const history = useHistory();

    const { productId } = useParams<ParamsType>();

    const [isLoadingCategories, setIsLoadingCategories] = useState(false);

    const [product, setProduct] = useState<Product>(getNewProduct());

    const [fileProduct, setFileProduct] = useState<FileProduct>(getNewFileProduct());

    const [categories, setCategories] = useState<Category[]>([]);

    const isEditing = productId !== 'create';

    const formTitle = isEditing ? 'Editar produto' : 'cadastrar um produto';

    useEffect(() => {
        if (isEditing) {
            makeRequest({ url: `/products/${productId}`})
            .then(response => {
                setProduct(response.data);
                setValue('name', response.data.name);
                setValue('price', response.data.price);
                setValue('description', response.data.description);
                setValue('imgUrl', response.data.imgUrl);
                setValue('categories', response.data.categories);

                if (response.data.fileProduct != null && response.data.fileProduct !== undefined) {
                    setFileProduct(response.data.fileProduct);
                }
            });
        }
    }, [productId, isEditing, setValue]);

    useEffect(() => {

        setIsLoadingCategories(true);

        makeRequest({ url: '/categories'})
        .then(response => {
             setCategories(response.data.content);
           }
        ).finally(() => { 
            setIsLoadingCategories(false);
        });

    }, []);

    const onSubmit = (formData: FormsState) => {

        if ( (fileProduct === null
              || fileProduct === undefined
                 || fileProduct.id === 0) && !isEditing) {

            toast.warn('É necessario adicionar a imagem para o produto !', {
                className: 'toast-notification',
                delay: 350,
                position: toast.POSITION.TOP_CENTER
            });
            return;
        }

        formData.fileProduct = fileProduct;

        makePrivateRequest({
            url: isEditing ? `/products/${productId}` : '/products',
            method: isEditing ? 'PUT' : 'POST',
            data: formData
        })
        .then((response) => {
            if (response.status === 200 || response.status === 201) {
                toast.info('Produto salvo com sucesso!');
                history.push('/admin/products');
            }
        }).catch(() => {
            toast.error('Erro ao salvar produto!');
        });
    }

    const onUploadSuccess = (idImg: number) => {

        if (fileProduct !== null && fileProduct !== undefined) {
            fileProduct.id = idImg;
        }
    }

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <BaseForm title={formTitle}>
                <div className="row">
                    <div className="col-6">
                        <div className="margin-bottom-30">
                            <input
                                {...register("name", {
                                    required: "Campo obrigatorio",
                                    minLength: { value: 5, message: 'O campo deve ter no mínimo 5 carecters'},
                                    maxLength: { value: 60, message: 'O campo deve ter no maximo 60 carecters'}
                                })}
                                type="text"
                                name="name"
                                className="form-control input-base"
                                placeholder="Nome do produto"
                            />
                            {errors.name && (
                                <div className="invalid-feedback d-block">
                                    {errors.name.message}
                                </div>
                            )}
                        </div>
                        <div className="margin-bottom-30">
                            <Controller
                                name="categories"
                                control={control}
                                rules={{ required: true }}
                                defaultValue=""
                                render={({ field }) => <Select
                                  {...field}
                                  options={categories}
                                  getOptionLabel={(option: Category) => option.name}
                                  getOptionValue={(option: Category) => String(option.id)}
                                  classNamePrefix="categories-select"
                                  placeholder="Categorias"
                                  isLoading={isLoadingCategories}
                                  isMulti
                                />}
                            />
                            {errors.categories && (
                                <div className="invalid-feedback d-block">
                                   Campo obrigatorio
                                </div>
                            )}
                        </div>
                        <div className="margin-bottom-30">
                            <PriceField control={control} />
                            {errors.price && (
                                <div className="invalid-feedback d-block">
                                   {errors.price.message}
                                </div>
                            )}
                        </div>
                        <div className="margin-bottom-30">
                            <ImageUpload 
                               product={product}
                               onUploadSuccess={onUploadSuccess}
                            />
                        </div>
                    </div>

                    <div className="cols-6">
                        <textarea
                           {...register("description", {required: "Campo obrigatorio"})}
                           name="description"
                           className="form-control input-base"
                           placeholder="Descrição"
                           cols={30}
                           rows={10} 
                        />
                        {errors.description && (
                            <div className="invalid-feedback d-block">
                               {errors.description.message}
                            </div>
                        )}
                    </div>
                </div>
            </BaseForm>
        </form>
    );
}

export default Form;