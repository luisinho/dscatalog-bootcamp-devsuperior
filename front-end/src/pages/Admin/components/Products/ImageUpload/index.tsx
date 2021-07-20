import { useState } from 'react';
import{ AxiosError } from 'axios';
import { toast } from 'react-toastify';
import { ReactComponent as UploadPlaceholder } from 'core/assets/images/upload-placeholder.svg';
import { makePrivateRequest } from 'core/utils/request';
import { Product } from 'core/types/Product';
import './styles.scss';

type Props = {    
    product: Product;
    onUploadSuccess: (idImg: number) => void;
}

const ImageUpload = ( { product, onUploadSuccess }: Props) => {

    const [uploadProgress, setUploadProgress] = useState<number>(0);

    const [extensao, setExtensao] = useState('');

    const [name, setName] = useState('');

    const [uploadedImgBase64, setUploadedImgBase64] = useState('');

    const imgUrl = uploadedImgBase64 || product.fileProduct?.encodedImage;

    const onUploadProgress = (progressEvent: ProgressEvent) => {

        const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);

        setUploadProgress(progress);
    }

    const uploadImage = (selectedImage: File) => {
        const paylaod = new FormData();
        paylaod.append('file', selectedImage);

        makePrivateRequest({ 
            url: '/files-product/image', 
            method: 'POST',
            data: paylaod,
            onUploadProgress
        }).then(response => {
            setExtensao(response.data.extensao);
            setUploadedImgBase64(response.data.encodedImage);
            setName(response.data.name);
            onUploadSuccess(response.data.id);
        }).catch((err: AxiosError) => {
            toast.error('Erro ao enviar o arquivo!');
        }).finally(() => {
            setUploadProgress(0);
        });
    }

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {

        const selectedImage = event.target.files?.[0];        

        if (selectedImage) {
            uploadImage(selectedImage);            
        }
    }

    return (
        <div className="row">
            <div className="col-6">
                <div className="upload-button-container">
                    <input
                        type="file"
                        id="upload"
                        hidden
                        accept="image/png, image/jpg"
                        onChange={handleChange}
                    />
                   <label htmlFor="upload">ADCIONAR IMAGEM</label>
                </div>
                <small className="upload-text-helper text-primary">
                    As imagens devem ser JPG ou PNG e não devem ultrapassar <strong>5 mb.</strong>
                </small>                
            </div>
            <div className="col-6 upload-placeholder">
                {uploadProgress > 0 && (
                    <>
                     <UploadPlaceholder />
                     <div className="upload-progress-container">
                        <div
                          className="upload-progress"
                          style={{width: `${uploadProgress}%`}}>

                        </div>
                     </div>
                    </>
                )}
                {(imgUrl && uploadProgress === 0) && (
                    <img
                       src={`data:${extensao};base64,${imgUrl}`}
                       alt={name}
                       className="uploaded-image"
                    />
                )}
                {product.imgUrl && !imgUrl && (
                  <img
                     src={product.imgUrl}
                     alt={product.name}
                     className="uploaded-image"
                  />
                )}
            </div>
        </div>
    );
}

export default ImageUpload;