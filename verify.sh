#!/bin/sh
HELM_NAME=sys-rest-user

kubectl port-forward --namespace default svc/$HELM_NAME 8081:8080 > build/$HELM_NAME.log 2>&1 &
curl http://localhost:8081/actuator/health

while [ $? -ne 0 ]; do
    kubectl port-forward --namespace default svc/$HELM_NAME 8081:8080 > build/$HELM_NAME.log 2>&1 &
    curl http://localhost:8081/actuator/health
    sleep 5
done
