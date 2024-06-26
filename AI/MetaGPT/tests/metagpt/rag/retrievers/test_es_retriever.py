import pytest
from llama_index.core.schema import Node

from metagpt.rag.retrievers.es_retriever import ElasticsearchRetriever


class TestElasticsearchRetriever:
    @pytest.fixture(autouse=True)
    def setup(self, mocker):
        self.doc1 = mocker.MagicMock(spec=Node)
        self.doc2 = mocker.MagicMock(spec=Node)
        self.mock_nodes = [self.doc1, self.doc2]

        self.mock_index = mocker.MagicMock()
        self.retriever = ElasticsearchRetriever(self.mock_index)

    def test_add_nodes(self):
        self.retriever.add_nodes(self.mock_nodes)

        self.mock_index.insert_nodes.assert_called()
