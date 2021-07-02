import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';

import { makePrivateRequest } from 'core/utils/request';
import BaseForm from '../../BaseForm';
import { useHistory } from 'react-router';

type FormsState = {
    name: string;
    price: number;  
    description: string;
    imageUrl: string;
}

const Form = () => {

    const { register, handleSubmit, formState: { errors } } = useForm<FormsState>();

    const history = useHistory();

    const onSubmit = (formData: FormsState) => {

        makePrivateRequest({url: '/products', method: 'POST', data: formData})
        .then((response) => {
            toast.info('Produto salvo com sucesso!');
            history.push('/admin/products');
        }).catch(() => {
            toast.error('Erro ao salvar produto!');
        });
    }

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <BaseForm title="cadastrar um produto">
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
                        {/*<select
                        value={formData.category}
                        name="category"
                        onChange={handleOnChange} className="form-control mb-5 input-base">
                            <option value="1">Livros</option>
                            <option value="3">Computadores</option>
                            <option value="2">Eletrônicos</option>
                        </select>*/}
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
                                {...register("imageUrl", {required: "Campo obrigatorio"})}
                                type="text"
                                name="imageUrl"
                                className="form-control input-base"
                                placeholder="Imagem do produto"
                            />
                            {errors.imageUrl && (
                                <div className="invalid-feedback d-block">
                                   {errors.imageUrl.message}
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