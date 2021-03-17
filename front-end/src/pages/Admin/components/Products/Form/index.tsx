import { makeRequest } from 'core/utils/request';
import React, { useState } from 'react';
import BaseForm from '../../BaseForm';

type FormsState = {
    name: string,
    price: number,
    category: number,
    description: string
}

type FormEvent = React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>;

const Form = () => {

    const [formData, setFormData] = useState<FormsState>({
        name: '',
        price: 0,
        category: 1,
        description: ''
    });

    const handleOnChange = (event: FormEvent) => {

        const name = event.target.name;
        const value = event.target.value;

        setFormData(data => ({...data, [name]: value}));
    }  

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const payload = {
            ...formData,
            imgUrl: 'https://img.vigiadepreco.com.br/61/038/fdce/167d7/61038fdce167d7d25c90dbfaf5ef5f9472289e390dfc09b8265db0b02648afa4.jpg',
            categories: [{ id: Number(formData.category) }]
        }        
        
        makeRequest({url: '/products', method: 'POST', data: payload})
          .then(() => {
              setFormData( {name: '', category: 1, price: 0, description: ''});


        });
    }

    return (
        <form onSubmit={handleSubmit}>
            <BaseForm title="cadastrar um produto">
                <div className="row">
                    <div className="col-6">
                    <input
                        type="text"
                        value={formData.name}
                        name="name"
                        className="form-control mb-5"
                        onChange={handleOnChange}
                        placeholder="Nome do produto"
                    />
                    <select
                       value={formData.category}
                       name="category"
                       onChange={handleOnChange} className="form-control mb-5">
                        <option value="1">Livros</option>
                        <option value="3">Computadores</option>
                        <option value="2">Eletrônicos</option>
                    </select>
                    <input
                        type="text"
                        value={formData.price}
                        name="price"
                        className="form-control"
                        onChange={handleOnChange}
                        placeholder="Preço"
                        />
                    </div>
                    <div className="cols-6">
                        <textarea
                           name="description"
                           value={formData.description}
                           onChange={handleOnChange}
                           className="form-control"
                           cols={30}
                           rows={10} />
                    </div>
                </div>            
            </BaseForm>
        </form>
    );
}

export default Form;