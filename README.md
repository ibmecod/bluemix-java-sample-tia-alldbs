# Twitter influence analyzer dao sample app

## How to get this running using cf ##
1. From the command line, log into Bluemix. 
``` cf login ```
2. Create a Bluemix SQL DB service as named in the manifest.yml file.
```cf cs sqldb sqldb_free sample-tia-sqldb```
3. Download the Project Zip from this github repository, extract the zip, and change the names of the application and services in the manifest.yml file to the names you will be using...
```---
declared-services:
  sample-tia-sqldb:
    label: sqldb
    plan: sqldb_free
applications:
- name: twitter_influence_analyzer_dao
  memory: 512M
  host: twitter_influence_analyzer_dao_${random-word}
  path: output/bluemix-tia-sample.war
  domain: mybluemix.net
  services:
   - sample-tia-sqldb
```

4. Navigate to the unzip directory from step 3 using the command line and push the app with the ```cf push``` command.

## One click deployment ##
You can deploy this applications directly to your bluemix by clicking this deployment button.
[![Deploy to Bluemix](https://bluemix.net/deploy/button.png)](https://bluemix.net/deploy?repository=https://github.com/ibmecod/bluemix-java-sample-tia-alldbs.git)

# Privacy Notice

Sample web applications that include this package may be configured to track deployments to [IBM Bluemix](https://www.bluemix.net/) and other Cloud Foundry platforms. The following information is sent to a [Deployment Tracker](https://github.com/IBM-Bluemix/cf-deployment-tracker-service) service on each deployment:

* Node.js package version
* Node.js repository URL
* Application Name (`application_name`)
* Space ID (`space_id`)
* Application Version (`application_version`)
* Application URIs (`application_uris`)

This data is collected from the `package.json` file in the sample application and the `VCAP_APPLICATION` environment variable in IBM Bluemix and other Cloud Foundry platforms. This data is used by IBM to track metrics around deployments of sample applications to IBM Bluemix to measure the usefulness of our examples, so that we can continuously improve the content we offer to you. Only deployments of sample applications that include code to ping the Deployment Tracker service will be tracked.
