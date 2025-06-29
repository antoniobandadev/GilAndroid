# GIL

![Logo](https://github.com/antoniobandadev/GilAndroid/blob/master/app/src/main/res/drawable/img_logo180x180.png)
### v1.0.0
------------

### Objetivo

Esta aplicación te permite administrar tus eventos e invitados de manera rápida y sencilla, directamente desde tu teléfono móvil.


### Logo

Es un codigo QR que al centro tiene una imagen con un candado, usuario y engrane.
Si se escanea te muestra el nombre de la app.

Este logo es porque la aplicacion se encarga de crear codigos QR para eventos y es una herramienta para administrar a tus invitados con seguridad.

### Dispositivos
Se eligieron dispositivos móviles (smartphones) como plataforma principal debido a su alta penetración en el mercado, portabilidad y facilidad de acceso para los usuarios. Los celulares permiten una experiencia de usuario inmediata y constante, lo cual es ideal para aplicaciones que requieren disponibilidad en cualquier momento y lugar.

### Versión
Versión mínima del sistema operativo: Android API 24 (Android 7.0 Nougat)

Se seleccionó API 24 como versión mínima por las siguientes razones:
- **Compatibilidad amplia**: Android 7.0 aún conserva una cuota significativa del mercado, lo cual permite abarcar una base amplia de usuarios sin comprometer funcionalidades modernas.

- **Soporte de características clave**: API 24 ofrece soporte nativo para mejoras de seguridad, notificaciones agrupadas, modo multiventana, y mejoras en rendimiento de background tasks, lo cual permite una implementación más eficiente y moderna de la app.

- **Compatibilidad con bibliotecas actuales**: La mayoría de las librerías modernas (como Retrofit, Room, Hilt, etc.) mantienen compatibilidad con esta versión, garantizando un desarrollo más sencillo sin perder acceso a herramientas actuales.

### Orientación
La orientación vertical (portrait) fue seleccionada para simplificar el diseño de interfaz y la experiencia de usuario. Esto garantiza una navegación más intuitiva y evita problemas de rediseño en modo horizontal, reduciendo el tiempo de desarrollo y minimizando errores visuales o de comportamiento.

En muchas aplicaciones móviles, el uso natural del dispositivo es en orientación vertical.

Limitar a portrait también mejora el rendimiento en pantallas pequeñas, evita redibujos innecesarios y proporciona una experiencia más consistente.

### Dependencias

- DataStore 1.0
- Glide 4.13.2
- Gson 2.11.0
- Hilt 2.56.2
- ImagePicker 1.6.1
- Interceptor 4.10.0
- Retrofit 2.11.0
- Room 2.5.0
- Rounded Image 2.3.0
- Security-crypto 1.1.0
- Shimmer 0.5.0
- Zxing 4.3.0
