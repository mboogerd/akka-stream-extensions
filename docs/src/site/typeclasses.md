---
layout: default
title:  "Type Classes"
section: "examples"
---
{% include_relative _tut/typeclasses.md %}

{% for x in site.tut %}
{% if x.section == 'examples' %}
- [{{x.title}}]({{site.baseurl}}{{x.url}})
{% endif %}
{% endfor %}

