ymaps.ready(init);

function init() {
    let myMap = new ymaps.Map("map", {
        center: [55.73, 37.75],
        zoom: 8
    }, {
        searchControlProvider: 'yandex#search'
    });

    let clusterer = getCluster()

    myMap.geoObjects.add(clusterer);
    myMap.setBounds(clusterer.getBounds(), {
        checkZoomRange: true
    });

    let sumGeoObjectsFilter = function (data, geoObjects, filterValue) {
        let sumOrders = 0;
        geoObjects.forEach(obj => sumOrders += obj.orders)
        return sumOrders
    };

    ymaps.template.filtersStorage.add('sumGeoObjectsOrders', sumGeoObjectsFilter);

    sumClusterOrders(myMap, clusterer)
}

function getCluster() {
    let clusterer = new ymaps.Clusterer({
        clusterIconShape: {
            type: 'Rectangle',
            coordinates: [[0, 0], [50, 50]]
        },

        clusterIconLayout: ymaps.templateLayoutFactory.createClass(
            '<div class="cluster-icon">{{ properties.geoObjects|sumGeoObjectsOrders }}</div>',
        ),

        clusterDisableClickZoom: true,
        clusterHideIconOnBalloonOpen: false,
        geoObjectHideIconOnBalloonOpen: false,
    });

    let getPointData = function (planfact) {
        let ut_rate = planfact.rateFact !== "null" ? 'Ставка согласована: ' + planfact.rateFact : 'Ставка не задана';
        //        let ut_rate = planfact.rateFact !== "null" ? 'Ставка согласована' : 'Ставка не задана';
        return {
            balloonContentBody:
                '<p>Станция: ' + planfact.station + '</p>' +
                '<p>Код: ' + planfact.departureStation + '</p>' +
                '<p> ' + ut_rate + ' </p>' +
                '<p>Вагоноотправок по плану: ' + planfact.planQuantity + '</p>' +
                '<p>Выполненных заявок: ' + planfact.planReady + '</p>' +
                '<p>Заявок в работе: ' + planfact.planInWork + '</p>' +
                '<p>Потребность в вагоноотправках: ' + planfact.shortage + '</p>'
            ,
            clusterCaption: '<strong>' + planfact.station + "(" + planfact.departureStation + ")" + '</strong>',
            iconContent: planfact.planQuantity
        };
    }

    let getPointOptions = function () {
        return {
            preset: 'islands#violetIcon'
        };
    }

    let points = getMapObjects()

    let geoObjects = [];
    for (let i = 0, len = points.length; i < len; i++) {
        let coordinates = [points[i].latitude, points[i].longitude]
        geoObjects[i] = new ymaps.Placemark(coordinates, getPointData(points[i]), getPointOptions());
        geoObjects[i].orders = points[i].planQuantity
    }
    clusterer.add(geoObjects);
    return clusterer
}

function getMapObjects() {
    let stationsRequest = new XMLHttpRequest();
    stationsRequest.open('GET', 'http://10.168.1.5:8080/result/station', false);
    stationsRequest.send(null);

    let planfactRequest = new XMLHttpRequest();
    planfactRequest.open('POST', 'http://10.168.1.5:8080/result/planfact', false);
    planfactRequest.send(null);

    if (stationsRequest.status === 200 && planfactRequest.status === 200) {
        let stations = JSON.parse(stationsRequest.responseText)
        let stationMap = {}
        stations.forEach(station => {
            stationMap[station.idStation] = station
        })

        let planfacts = JSON.parse(planfactRequest.responseText)

        planfacts.forEach(planfact => {
            let station = stationMap[planfact.departureStation]

            planfact.station = station ? station.nameStation : ""
            planfact.latitude = station ? station.latitude : ""
            planfact.longitude = station ? station.longitude : ""
        })

        return planfacts.filter(planfact => planfact.latitude !== "" && planfact.longitude !== "")
    }

    alert("Ошибка загрузки данных. Повторите запрос позже")
}

function sumClusterOrders(map, clusterer) {
    map.events.add('boundschange', function () {
        let clusters = clusterer.getClusters();
        clusters.forEach(cluster => {
            let orderSum = 0;
            cluster.properties._data.geoObjects.forEach(obj => {
                orderSum += parseFloat(obj.orders);
            });
            cluster.totalOrders = orderSum;
        });
    });
}
