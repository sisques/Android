# Ebrozon Android App
## Instrucciones para importar el proyecto en Android Studio:
1. Start a new Android Studio project  
2. Login Activity (Name: Ebrozon, Package name: es.unizar.eina.ebrozon, API 21)  
3. Esperar a que se genere todo y se indexe  
4. Cerrar Android Studio y abrir terminal con git en la carpeta del proyecto  
5. Ejecutar:  
  git init  
  git remote add origin https://github.com/unizar-30226-2019-02/Android.git  
  git fetch --all  
  git reset --hard origin/master  
  git branch --set-upstream-to=origin/master master  
