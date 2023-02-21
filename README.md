# Proomp
A [Clojure](https://clojure.org/) workspace for [Stable Diffusion](https://stability.ai/blog/stable-diffusion-public-release).

Runs on top of Python, using PyTorch with the Nvidia CUDA Toolkit.

## Hardware Requirements
An Nvidia GPU with at least 10GB VRAM should work fine for generating HD animations.
VRAM of 8GB or less may work at low resolutions after some tuning.
GPUs from different manufacturers may require some tinkering to get PyTorch working.

## Setup
### Git
Install Git https://git-scm.com/downloads.

### Nvidia CUDA compiler (NVCC)
Check your Nvidia CUDA compiler version with `nvcc --version`.
If it's missing, see [install NVCC](https://docs.nvidia.com/cuda/cuda-compiler-driver-nvcc/).

### Python
Install Python 3.9 https://www.python.org/downloads/.
Newer versions may require installation of a nightly PyTorch build.

### Python 3.9 Dependencies

    pip install -r requirements-3.9.txt

### Alternative for Python 3.11 and CUDA 11.8
Something like the following *may* work:

    pip install -r requirements.txt

In case of problems, see `Trouble-Shooting` and `Manual python dependency setup` below.

### Download and prepare Stable Diffusion model
> &#x26a0;&#xfe0f; Requires an account on https://huggingface.co.

    cd models
    git lfs install
    git lfs clone https://huggingface.co/stabilityai/stable-diffusion-2-1

### Leiningen And Clojure
> &#x2139;  Use JDK 17 or similar, i.e. from https://adoptium.net/.

Install Leiningen from https://leiningen.org/.

The Clojure version is set in Leiningens build file `project.clj`.

## Configuration
Set your `python-dir` and your `workspace-path` in `proomp.config`.

## Usage
* Check and adjust the parameters in `proomp.core`.
  * Set an [active-mode](https://github.com/Tok/proomp/blob/main/src/proomp/core.clj#L11).
      * `::images` Generates different images from a prompt. Can be used to find a good start seed.
      * `::animation` Generates frames for a prompt and a start seed.
      * `::video` Creates a video from the generated frames.
  * Set a wittyful prompt.
    * Optionally provide a negative prompt and change the prompt additions according to your needs.
  * Consider changing the active [pipe setup](https://github.com/Tok/proomp/blob/main/src/proomp/domain/pipe/pipe_setup.clj) and [resolutions](https://github.com/Tok/proomp/blob/main/src/proomp/domain/image/resolution.clj).
    * Perhaps tune other settings in the [domain sources](https://github.com/Tok/proomp/blob/main/src/proomp/domain).
  * Finally, run the -main function in `proomp.core` (see core.clj) after preparing your run configuration with the VM arguments below.

Enable native access:

    --enable-native-access=ALL-UNNAMED

Enable experimental JDK features (required with JDK 17 and similar):

    --add-modules jdk.incubator.foreign,jdk.incubator.vector

## Test Reports
* [Unit Test Results](https://tok.github.io/proomp/test-report)
* [Cloverage Report](https://tok.github.io/proomp/cloverage)

## Trouble-Shooting
`proomp.core-test` contains tests to check if Cuda is available
and to check if PyTorch bindings are working.

In case of problems, consider the following:
- PyTorch needs to be installed with an active cuda toolkit.
- The console command `nvcc --version` should return `Cuda compilation tools` with version and build number.
  - make sure to match your CUDA version in `requirements-3.9.txt` or [install torch separately](https://pytorch.org/get-started/locally/).

### PyTorch Installation

In case of problems, see: https://pytorch.org/get-started/locally/

### Manual python dependency setup
In case of problems with the dependencies in `requirements.txt`,
something like the following *may* work:

    pip install --upgrade pip setuptools wheel
    pip install --upgrade numpy
    pip install --upgrade Pillow
    pip3 install --pre torch torchvision torchaudio --index-url https://download.pytorch.org/whl/nightly/cu118
    pip install diffusers[torch]==0.11.0
    pip install --upgrade scikit-image --pre
    pip install --upgrade transformers
    pip install --upgrade accelerate

* numpy and Pillow are required to install torch. 
* torch requires the CUDA compiler (NVCC).
  * For Python versions > 3.9, try a nightly torch build since there's no official support.
* diffusers require torch and shouldn't be higher than version 0.11.0 (for now).
* scikit-image depends on numpy and may require VS C++ Build Tools to compile.
