-Questo progetto dipende dal progetto firmapiulib
Per essere correttamente configurato, i file settings.gradle e build.gradle devono essere salvati nella directory 'parent' e il build finale
dei progetti firmapiulib e firmapiu-cli devono essere eseguiti dalla directory parent

parent-directory
	settings.gradle
	build.gradle
	|
	|-	firmapiu-cli
	|
	|-	firmapiulib
per eseguire il build del progetto, dalla directory parent si fa: 

gradle firmapiu-cli:build




-Aggiunto supporto per linkare il progetto ad eclipse:
Dalla directory parent si fa:

gradle firmapiu-cli:eclipse

