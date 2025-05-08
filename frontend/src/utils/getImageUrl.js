export function getImageUrl(path) {
  const BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
  if (!path) return '';
  if (path.startsWith('http://') || path.startsWith('https://')) return path;
  return `${BASE_URL}${path}`;
} 