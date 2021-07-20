export type ProductsResponse = {
    content: Product [];
    totalPages: number;
}

export type Product = {
    id: number;
    name: string;
    description: string;
    price: number;
    imgUrl: string;
    date: Date;
    fileProduct: FileProduct;
    categories: Category[];
}

export type Category = {
    id: number;
    name: string;
}

export type FileProduct = {

    id: number;
    name: string;
    originalFilename: string;
    encodedImage: string;
    extensao: string;
    size:number;
}

export const getNewProduct = () => {

    const product = {
        id: 0,
        name: '',
        description: '',
        price: 0,
        imgUrl: '',
        date: new Date(),
        fileProduct: getNewFileProduct(),
        categories: [getNewCategory()]
    }

    return product as Product;
}

export const getNewCategory = () => {

    const category = {
        id: 0,
        name: ''
    }

    return category as Category;
}

export const getNewFileProduct = () => {

    const fileProduct = {
        id: 0,
        name: '',
        originalFilename: '',
        encodedImage: '',
        extensao: '',
        size: 0
    }

    return fileProduct as FileProduct;
}