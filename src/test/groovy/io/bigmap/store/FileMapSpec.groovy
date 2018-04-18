package io.bigmap.store

import io.bigmap.store.map.FileMap
import io.bigmap.store.map.Key
import io.bigmap.store.map.KeyDuplicationException
import io.bigmap.store.map.infrastructure.InMemoryIndex
import spock.lang.Specification

class FileMapSpec extends Specification {
    // TODO unify path for all systems
    // TODO clean files

    String filePath

    def setup() {
        filePath = '/tmp/bigmap'
    }

    def "should read/write single value"() {
        given:
        FileMap map = new FileMap(filePath, new InMemoryIndex())
        Key k = Key.of('1', 'id123')

        when:
        map.add(k, "greatvalue123")

        then:
        map.get(k).get() == "greatvalue123"
    }

    def "should read/write multiple values"() {
        given:
        FileMap map = new FileMap(filePath, new InMemoryIndex())

        when:
        map.add(Key.of('1', 'id123'), "great value123")

        and:
        map.add(Key.of('2', 'id123'), "different value123")

        then:
        map.get(Key.of('1', 'id123')).get() == "great value123"

        and:
        map.get(Key.of('2', 'id123')).get() == "different value123"
    }

    def "should throw error when trying to update existing key"() {
        given:
        FileMap map = new FileMap(filePath, new InMemoryIndex())
        Key k = Key.of('1', 'id123')

        and:
        map.add(k, "greatvalue123")

        when:
        map.add(k, "new")

        then:
        thrown(KeyDuplicationException)
    }

    def "should return newest object"() {
        given:
        FileMap map = new FileMap(filePath, new InMemoryIndex())
        def id = 'id123'

        and:
        map.add(Key.of('1', id), "great value123")
        map.add(Key.of('2', id), "great value1234")
        map.add(Key.of('3', id), "great value12345")

        when:
        String head = map.getHead("id123").get()

        then:
        head == "great value12345"
    }

    def "should return empty optional if key was not found"() {
        given:
        FileMap map = new FileMap(filePath, new InMemoryIndex())

        when:
        def result = map.get(Key.of('1', '1234'))

        then:
        !result.isPresent()

        when:
        result = map.getHead('1234')

        then:
        !result.isPresent()
    }

    def "should delete key"() {
        given:
        FileMap map = new FileMap(filePath, new InMemoryIndex())
        def id = 'id123'
        def id2 = "id2"

        and:
        map.add(Key.of('1', id2), "meh")

        map.add(Key.of('1', id), "great value123")
        map.add(Key.of('2', id), "great value1234")
        map.add(Key.of('3', id), "great value12345")

        when:
        map.delete(id)

        then:
        !map.getHead(id).isPresent()
        !map.get(Key.of('1', id)).isPresent()
        !map.get(Key.of('2', id)).isPresent()
        !map.get(Key.of('3', id)).isPresent()

        and:
        map.getHead(id2).isPresent()
        map.get(Key.of('1', id2)).isPresent()
    }
}