import { useEffect, useState } from 'react';
import { useHistory, useParams } from 'react-router';
import { useForm, Controller } from 'react-hook-form';
import { toast } from 'react-toastify';
import Select from 'react-select';

import { Category } from 'core/types/Product';
import { makePrivateRequest, makeRequest } from 'core/utils/request';
import BaseForm from '../../BaseForm';
import './styles.scss';

type FormsState = {
    name: string;
    price: number;
    description: string;
    imgUrl: string;
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

    const [categories, setCategories] = useState<Category[]>([]);

    const isEditing = productId !== 'create';

    const formTitle = isEditing ? 'Editar produto' : 'cadastrar um produto';

    useEffect(() => {
        if (isEditing) {
            makeRequest({ url: `/products/${productId}`})
            .then(response => {
                setValue('name', response.data.name);
                setValue('price', response.data.price);
                setValue('description', response.data.description);
                setValue('imgUrl', response.data.imgUrl);
                setValue('categories', response.data.categories);
            });
        }
    }, [productId, isEditing, setValue]);

    useEffect(() => {
        setIsLoadingCategories(true);
        makeRequest({ url: '/categories'})
        .then(response => setCategories(response.data.content))
        .finally(() => setIsLoadingCategories(false));

    }, []);

    const onSubmit = (formData: FormsState) => {

        makePrivateRequest({
            url: isEditing ? `/products/${productId}` : '/products',
            method: isEditing ? 'PUT' : 'POST',
            data: formData
        })
        .then((response) => {
            toast.info('Produto salvo com sucesso!');
            history.push('/admin/products');
        }).catch(() => {
            toast.error('Erro ao salvar produto!');
        });
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
                            <input
                                {...register("price", {required: "Campo obrigatorio"})}
                                type="number"
                                name="price"
                                className="form-control input-base"
                                placeholder="Preço"
                            />
                            {errors.price && (
                                <div className="invalid-feedback d-block">
                                   {errors.price.message}
                                </div>
                            )}
                        </div>
                        <div className="margin-bottom-30">
                            <input
                                {...register("imgUrl", {required: "Campo obrigatorio"})}
                                type="text"
                                name="imgUrl"
                                className="form-control input-base"
                                placeholder="Imagem do produto"
                            />
                            {errors.imgUrl && (
                                <div className="invalid-feedback d-block">
                                   {errors.imgUrl.message}
                                </div>
                            )}
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