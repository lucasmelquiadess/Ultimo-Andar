import { labels } from '../utils/formatters';

type Props = {
  value: keyof typeof labels;
};

export function StatusBadge({ value }: Props) {
  return <span className={`status status-${String(value).toLowerCase()}`}>{labels[value]}</span>;
}
