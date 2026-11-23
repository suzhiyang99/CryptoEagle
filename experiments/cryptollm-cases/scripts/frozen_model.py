"""Released CryptoLLM model loading and slice inference; no training.

Preserves the previous inference implementation: CPU checkpoint loading,
512-token inputs, and accumulation of predictions across all batches.
Case aggregation remains in 03_predict.py.
"""
import sys
from pathlib import Path

import torch
from transformers import RobertaTokenizer, T5Config, T5ForConditionalGeneration

ROOT = Path(__file__).resolve().parents[3]
TOOL = ROOT / "external-tools" / "cryptollm"
MODEL = TOOL / "CryptoLLM_codet5.bin"
PRETRAIN = "Salesforce/codet5-small"
BLOCK = 512

sys.path.insert(0, str(TOOL))
from detect_codet5 import CodeT5  # noqa: E402


def load_model(device):
    config = T5Config.from_pretrained(PRETRAIN)
    config.num_labels = 2
    tokenizer = RobertaTokenizer.from_pretrained(PRETRAIN)
    encoder = T5ForConditionalGeneration.from_pretrained(PRETRAIN, config=config)
    model = CodeT5(encoder, config, tokenizer)
    model.load_state_dict(torch.load(str(MODEL), map_location="cpu"))
    model.to(device).eval()
    return model, tokenizer


@torch.no_grad()
def predict(codes, model, tokenizer, device, batch_size=32):
    preds = []
    for start in range(0, len(codes), batch_size):
        chunk = codes[start:start + batch_size]
        ids = [
            tokenizer.encode(" ".join(code.split()), max_length=BLOCK,
                             padding="max_length", truncation=True)
            for code in chunk
        ]
        probs = model(torch.tensor(ids).to(device), labels=None)
        preds.extend(probs.cpu().numpy().argmax(-1).tolist())
    return preds
