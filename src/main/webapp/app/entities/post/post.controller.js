(function() {
    'use strict';

    angular
        .module('feedditApp')
        .controller('PostController', PostController);

    PostController.$inject = ['Post'];

    function PostController(Post) {

        var vm = this;

        vm.posts = [];

        loadAll();

        function loadAll() {
            Post.query(function(result) {
                vm.posts = result;
                vm.searchQuery = null;
            });
        }
    }
})();
