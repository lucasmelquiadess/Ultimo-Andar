import { formatCurrencyInput, parseCurrencyInput } from '../utils/formatters';

type Props = {
  value?: number;
  onChange: (value: number) => void;
  disabled?: boolean;
  required?: boolean;
};

export function CurrencyInput({ value, onChange, disabled, required }: Props) {
  return (
    <input
      disabled={disabled}
      inputMode="numeric"
      required={required}
      value={formatCurrencyInput(value)}
      onChange={(event) => onChange(parseCurrencyInput(event.target.value))}
    />
  );
}
