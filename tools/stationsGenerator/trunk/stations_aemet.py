#!/usr/bin/env python3

"""* Copyright (c) ONTOLOGY ENGINEERING GROUP: UNIVERSIDAD POLITÉCNICA DE MADRID, 2011
* Todos los derechos reservados.
* Título: Generate AEMET Stations
* Autor: José Mora López"""

from urllib.parse import quote

prefixes =('@base <http://aemet.linkeddata.es/ontology/> .\n' +
           '@prefix aemet: <http://aemet.linkeddata.es/ontology/> .\n' +
           '@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> .\n' +
           '@prefix estacion: <http://aemet.linkeddata.es/resource/Estacion/> .\n' +
           '@prefix punto: <http://aemet.linkeddata.es/resource/Point/> .\n' +
           '@prefix prop: <http://aemet.linkeddata.es/ontology/> .\n\n\n')

stationTemplate = ('estacion:Estacion_%s a aemet:Estacion;\n' +
                   '\tprop:indclim "%s";\n' +
                   '\tprop:indsinop "%s";\n' +
                   '\tprop:nombreEstacion "%s";\n' +
                   '\tgeo:location punto:GeoPos_%s;\n' +
                   '\t.\n\n')

geoposTemplate = ('punto:GeoPos_%s a geo:Point;\n' +
                  '\tgeo:long "%s";\n' +
                  '\tgeo:lat "%s";\n' +
                  '\tgeo:alt "%s";\n' +
                  '\t.\n\n')
#convertCoordinates
cc = lambda n: str((-1 if n[6] in 'SW' else 1) * (int(n[:2]) + int(n[2:4])/60 + int(n[4:6])/3600))

# 0Indicativo;1NOMBRE;2Provincia;3ALTITUD;4LATITUD;5LONGITUD;6INDSINOP
with open('stations.rdf', 'w', encoding='utf8') as outf:
  with open('maestro.csv', 'r') as inf:
    outf.write(prefixes)
    for l in inf:
      e = l[:-1].split(';')
      if len(e) >= 7 and len(e[6]) > 0:
        outf.write(stationTemplate%(e[6], e[0], e[6], e[1], e[6]))
        outf.write(geoposTemplate%(e[6], cc(e[5]), cc(e[4]), e[3]))
