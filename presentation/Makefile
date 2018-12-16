DOC	= slides
TEXSRC	= *.tex


all: $(DOC).pdf

ps: $(DOC).ps

$(DOC).pdf: $(DOC).tex $(TEXSRC)
	compile_doc.sh $(DOC)

$(DOC).ps: $(DOC).pdf
	pdftops $(DOC).pdf $(DOC).ps

clean:
	@rm -v -rf *~ *.log $(DOC).toc $(DOC).aux *.dvi $(DOC).out $(DOC).nav $(DOC).snm \
	$(DOC).pdf $(DOC).ps

ispell:
	ispell -d russian *tex

aspell:
	bash -c 'for i in *tex; do aspell -d ru -t -c $$i; done'
