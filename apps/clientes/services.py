import requests
from typing import Optional, Tuple

class GeocodingService:
    """Servicio para geocodificación usando OpenStreetMap Nominatim API"""

    BASE_URL = "https://nominatim.openstreetmap.org/search"

    @staticmethod
    def geocode_address(direccion: str, ciudad: str = "", codigo_postal: str = "") -> Optional[Tuple[float, float]]:
        """
        Geocodifica una dirección y devuelve (latitud, longitud)
        """
        # Construir la query
        query_parts = []
        if direccion:
            query_parts.append(direccion)
        if ciudad:
            query_parts.append(ciudad)
        if codigo_postal:
            query_parts.append(codigo_postal)

        query = ", ".join(query_parts)

        if not query:
            return None

        params = {
            'q': query,
            'format': 'json',
            'limit': 1,
            'countrycodes': 'ES,AR,MX,CO,PE,CL,EC,BO,PY,UY,VE'  # Países de habla hispana
        }
        headers = {
            'User-Agent': 'proyecto-dyr/1.0 (contacto@proyecto.local)',
            'Accept-Language': 'es'
        }

        def request_coords(params_to_send):
            response = requests.get(GeocodingService.BASE_URL, params=params_to_send, headers=headers, timeout=10)
            if response.status_code == 403 and 'countrycodes' in params_to_send:
                no_cc = dict(params_to_send)
                no_cc.pop('countrycodes', None)
                response = requests.get(GeocodingService.BASE_URL, params=no_cc, headers=headers, timeout=10)
            response.raise_for_status()
            return response.json()

        try:
            data = request_coords(params)
            if data:
                return float(data[0]['lat']), float(data[0]['lon'])

            if codigo_postal:
                params_without_postal = dict(params)
                params_without_postal.pop('countrycodes', None)
                params_without_postal['q'] = ', '.join([direccion, ciudad]) if ciudad else direccion
                data = request_coords(params_without_postal)
                if data:
                    return float(data[0]['lat']), float(data[0]['lon'])
        except (requests.RequestException, ValueError, KeyError):
            pass

        return None