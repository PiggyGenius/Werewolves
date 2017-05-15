# werewolves-game

## Connexion à la base de données

La connexion à la base de données de l'Ensimag se fait avec ojdbc7, il faut donc inclure le fichier src/werewolves/ojdbc7 dans le dossier apache-tomcat/lib pour que la connexion se fasse correctement.

Il faut également ajouter un rôle dans le fichier apache-tomcat/conf/tomcat-users.xml afin que Tomcat puisse se connecter à la base de données avec l'identifiant et le mot de passe souhaité:

	<role rolename="manager-gui"/>
	<role rolename="manager-script"/>
	<user username="carrel" password="carrel" roles="manager-gui, manager-script"/>
	</tomcat-users>

## Déployer le site web

Une des fonctions PL/SQL créées retourne un curseur et l'application doit donc pouvoir utiliser le type de données OracleTypes.CURSOR.
Pour importer la classe OracleTypes, il faut inclure la dépendance ojdbc7 dans le pom.xml, ce qui est déjà fait pour le projet livré.
Il est également nécessaire d'installer la librairie, et ceci doit être fait sur la machine qui va déployer l'application sur le serveur Tomcat:

	mvn install:install-file -Dfile=ojdbc7.jar -DgroupId=com.oracle 
	-DartifactId=ojdbc7 -Dversion=12.1.0.1.0 -Dpackaging=jar

Une fois la configuration terminée, il faut lancer le serveur Tomcat avec apache-tomcat/bin/startup.sh, puis utiliser Maven pour déployer le site web sur le serveur:

	mvn tomcat:deploy
	ou
	mvn tomcat:redeploy

Attention, tomcat nécessite d'avoir renseigné ses identifiants dans le fichier ~/.m2/settings.xml.
Le site web est accessible avec l'url suivante: http://localhost:8080/werewolves/.

