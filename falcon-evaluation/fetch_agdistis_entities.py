import stanza
import requests
import time

nlp = stanza.Pipeline('en')
agdistis_endpoint = "http://127.0.0.1:8282/AGDISTIS"
def identify_entities(text):
    '''
    returns annotated text in the form that AGDISTIS accepts
    <entity>Barack Obama</entity> was born in <entity>Hawaii</entity>.
    :param text:
    :return:
    '''
    annotated_text = ""
    doc = nlp(text)
    entities_found = []
    for sentence in doc.sentences:
        offset = 0
        for ent in sentence.ents:
            ent = ent.to_dict()
            start = ent["start_char"] + offset
            end = ent["end_char"] + offset

            replace_ent_in_text = "".join(["<entity>", text[start:end], "</entity>"])
            #print(replace_ent_in_text)
            annotated_text = "".join([text[:start], replace_ent_in_text, text[end:]])
            offset += len("<entity>") + len("</entity>")
            #print(annotated_text)
            text = annotated_text
            entities_found.append(ent['text'])
    return annotated_text, entities_found

def call_agdistis(text):
    '''
    returns an object that could be given as input to falcon
    :cvar
    '''
    entities = []
    input, entities_found = identify_entities(text)
    PARAMS = {"type":"agdistis", "text":input.encode('utf-8')}
    try:
        response = requests.post(agdistis_endpoint, data=PARAMS)
        response.encoding = 'utf-8'
        data = response.json()
        for i, obj in enumerate(data):
            entities.append([entities_found[i], obj["disambiguatedURL"], 100, 0])
    except:
        print("no entities found")
    return entities

if __name__ == '__main__':
    start = time.time()
    print(call_agdistis('Barack Obama was born in Hawaii'))
    print(time.time()-start)


