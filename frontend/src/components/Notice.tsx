type Props = {
  message?: string;
  type?: 'success' | 'error' | 'info';
};

export function Notice({ message, type = 'info' }: Props) {
  if (!message) return null;
  return <div className={`notice notice-${type}`}>{message}</div>;
}
