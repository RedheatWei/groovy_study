package org.acme.groovy

class Song{
    def name
    def artist
    def genre
    String toString(){
        "${name}, ${artist}, ${getGenre()}"
    }
    def getGenre(){
        genre.toUpperCase()
    }
}
def sng = new Song(name:"Le Freak",
        artist:"Chic", genre:"Disco")
def sng2 = new Song(name:"Kung Fu Fighting", genre:"Disco")
println(sng2.artist?.toUpperCase())
def sng3 = new Song()
sng3.name = "Funkytown"
sng3.artist = "Lipps Inc."
sng3.setGenre("Disco")
println(sng3.genre)
assert sng3.getArtist() == "Lipps Inc."


