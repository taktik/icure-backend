import {ProseEditor} from './prose-editor'
import './prose-editor'
import './test-prose-editor.html'
import * as assert from "assert";

declare function fixture<T>(element: string):T

describe('prose-editor', function() {
    it('should have a working editor', function(done) {
        var element = fixture<ProseEditor>('BasicTestFixture');
        assert.notEqual(element.editorView, undefined);
        done()
    });
});
