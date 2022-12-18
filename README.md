# Proomp

A Clojure workspace for Stable Diffusion.

## Installation

### Diffusers

    pip3 install --upgrade git+https://github.com/huggingface/diffusers.git transformers accelerate scipy

### PyTorch

    pip3 install torch torchvision torchaudio --extra-index-url https://download.pytorch.org/whl/cu117

### Download Stable Diffusion Model

    cd models
    git lfs install
    git lfs clone https://huggingface.co/stabilityai/stable-diffusion-2-1

## Usage

Run proomp.core with the VM arguments:

    --add-modules jdk.incubator.foreign,jdk.incubator.vector
    --enable-native-access=ALL-UNNAMED
