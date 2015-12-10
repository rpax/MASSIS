#!/bin/bash
rm -rf site
rm site.zip
hugo -d site --theme="liquorice"
cd site
zip -r ../site.zip ./*
cd ..
