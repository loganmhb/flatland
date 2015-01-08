# flatland

A flat-file CMS using Hiccup for templating.

## Usage

I wanted to do a blog without leaving Clojure-land (Octopress was too much overhead). So the functionality is quite limited:

Posts (format: yaml frontmatter, a "---" separator on its own line, followed by the post in markdown format) go in the "resources/posts" directory.

Static pages (i.e. About Me, etc), formatted similarly, go in the "resources/pages" directory.

CSS and Javascript go in the "public" directory.

The templates are located in "src/flatland/templates.clj" because they're Clojure code, sort of. Not sure what the best practice is for that sort of thing. These get loaded at runtime. You could probably even reload them while the app was running if you wanted to get crazy; I haven't tried it, because it's a blog.

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
