# Proomp

A Clojure workspace for Stable Diffusion.

Runs on top of Python 3.9 using PyTorch with CUDA Toolkit 11.6.

## Installation

### Python

Install Python 3.9 with Pip https://www.python.org/downloads/

### Hugging Face Diffusers

    pip3 install --upgrade git+https://github.com/huggingface/diffusers.git transformers accelerate scipy

### PyTorch

    pip3 install torch torchvision torchaudio --extra-index-url https://download.pytorch.org/whl/cu117

### Download Stable Diffusion Model

> &#x26a0;&#xfe0f; Requires an account on https://huggingface.co

    cd models
    git lfs install
    git lfs clone https://huggingface.co/stabilityai/stable-diffusion-2-1

## Configuration

- Set your `python-dir` and your `workspace-path` in `proomp.config`

## Usage
- Consider tuning the default values in `proomp.constants`
- Check and adjust the parameters in `proomp.core`.

Run proomp.core with the VM arguments:

    --add-modules jdk.incubator.foreign,jdk.incubator.vector
    --enable-native-access=ALL-UNNAMED

## Trouble-Shooting

`proomp.core-test` contains tests to check if Cuda is available
and to check if PyTorch bindings are working.

In case of problems, consider the following:
- PyTorch needs to be installed with an active cuda toolkit.
- The console command `nvcc --version` should return something.
