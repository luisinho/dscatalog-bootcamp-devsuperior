import { Control, Controller } from "react-hook-form";
import CurrencyInput from 'react-currency-input-field';
import { FormsState } from './';

type Props = {
    control: Control<FormsState>
}

const PriceField = ({ control }: Props) => (

    <Controller
      name="price"
      defaultValue=""
      control={control}
      rules={{ required: 'Campo obrigatório' }}
      render={({ field }) => (
          <CurrencyInput
            placeholder="Preço"
            className="form-control input-base"
            value={field.value}
            intlConfig={{locale: 'pt-BR', currency: 'BRL' }}
            onValueChange={field.onChange}
          />
      )}
    />
);

export default PriceField;